package com.example.quizapp.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.quizapp.domain.History
import com.example.quizapp.data.firebase.history.HistoryRepository as FirebaseHistoryRepository
import com.example.quizapp.data.room.history.HistoryRepository as RoomHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.UUID

class OfflineAwareHistoryRepository(
    private val context: Context,
    private val firebaseRepository: FirebaseHistoryRepository,
    private val roomRepository: RoomHistoryRepository
) {

    suspend fun insert(history: History) {
        // Always save to Room first for offline support
        val historyWithId = if (history.id.isEmpty()) {
            history.copy(id = UUID.randomUUID().toString())
        } else {
            history
        }

        try {
            roomRepository.insert(historyWithId)
            Log.d("OfflineAwareHistoryRepo", "Saved history to Room: ${historyWithId.id}")
        } catch (e: Exception) {
            Log.e("OfflineAwareHistoryRepo", "Error saving to Room", e)
            throw e // Don't continue if Room save fails
        }

        // Try to save to Firebase if online
        if (isOnline()) {
            try {
                firebaseRepository.insert(historyWithId)
                // Mark as synced in Room after successful Firebase save
                roomRepository.markAsSynced(historyWithId.id)
                Log.d("OfflineAwareHistoryRepo", "Saved history to Firebase and marked as synced: ${historyWithId.id}")
            } catch (e: Exception) {
                Log.w("OfflineAwareHistoryRepo", "Failed to save to Firebase, will sync later", e)
                // Entry remains unsynced in Room
            }
        } else {
            Log.d("OfflineAwareHistoryRepo", "Offline - history will sync when online")
        }
    }

    fun getAll(): Flow<List<History>> = flow {
        if (isOnline()) {
            try {
                // Sync unsynced entries first
                syncPendingToFirebase()

                // Then load from Firebase
                firebaseRepository.getAll().collect { firebaseHistories ->
                    // Get unsynced local entries
                    val unsyncedLocal = roomRepository.getUnsyncedEntries()

                    // Merge Firebase data with unsynced local data
                    val mergedHistories = (firebaseHistories + unsyncedLocal)
                        .distinctBy { it.id }
                        .sortedByDescending { it.date }

                    // Save Firebase data to Room (mark as synced)
                    firebaseHistories.forEach { history ->
                        roomRepository.insert(history)
                        roomRepository.markAsSynced(history.id)
                    }

                    emit(mergedHistories)
                }
            } catch (e: Exception) {
                Log.w("OfflineAwareHistoryRepo", "Firebase failed, loading from Room", e)
                roomRepository.getAll().collect { roomHistories ->
                    emit(roomHistories)
                }
            }
        } else {
            Log.d("OfflineAwareHistoryRepo", "Offline mode - loading from Room")
            roomRepository.getAll().collect { roomHistories ->
                emit(roomHistories)
            }
        }
    }.catch { e ->
        Log.e("OfflineAwareHistoryRepo", "Error getting histories", e)
        roomRepository.getAll().collect { roomHistories ->
            emit(roomHistories)
        }
    }

    fun getAllByUser(userId: String): Flow<List<History>> = flow {
        if (isOnline()) {
            try {
                // Sync unsynced entries first
                syncPendingToFirebase()

                firebaseRepository.getAllByUser(userId).collect { firebaseHistories ->
                    // Get unsynced local entries for this user
                    val unsyncedLocal = roomRepository.getUnsyncedEntries()
                        .filter { it.userId == userId }

                    // Merge Firebase data with unsynced local data
                    val mergedHistories = (firebaseHistories + unsyncedLocal)
                        .distinctBy { it.id }
                        .sortedByDescending { it.date }

                    // Save Firebase data to Room (mark as synced)
                    firebaseHistories.forEach { history ->
                        roomRepository.insert(history)
                        roomRepository.markAsSynced(history.id)
                    }

                    emit(mergedHistories)
                }
            } catch (e: Exception) {
                Log.w("OfflineAwareHistoryRepo", "Firebase failed, loading from Room", e)
                roomRepository.getAllByUser(userId).collect { roomHistories ->
                    emit(roomHistories)
                }
            }
        } else {
            Log.d("OfflineAwareHistoryRepo", "Offline mode - loading from Room")
            roomRepository.getAllByUser(userId).collect { roomHistories ->
                emit(roomHistories)
            }
        }
    }.catch { e ->
        Log.e("OfflineAwareHistoryRepo", "Error getting user histories", e)
        roomRepository.getAllByUser(userId).collect { roomHistories ->
            emit(roomHistories)
        }
    }

    fun getUserQuizHistory(userId: String, quizId: String): Flow<List<History>> = flow {
        if (isOnline()) {
            try {
                firebaseRepository.getUserQuizHistory(userId, quizId).collect { histories ->
                    saveToRoom(histories)
                    emit(histories)
                }
            } catch (e: Exception) {
                Log.w("OfflineAwareHistoryRepo", "Firebase failed, loading from Room", e)
                roomRepository.getUserQuizHistory(userId, quizId).collect { roomHistories ->
                    emit(roomHistories)
                }
            }
        } else {
            Log.d("OfflineAwareHistoryRepo", "Offline mode - loading from Room")
            roomRepository.getUserQuizHistory(userId, quizId).collect { roomHistories ->
                emit(roomHistories)
            }
        }
    }.catch { e ->
        Log.e("OfflineAwareHistoryRepo", "Error getting quiz history", e)
        roomRepository.getUserQuizHistory(userId, quizId).collect { roomHistories ->
            emit(roomHistories)
        }
    }

    private suspend fun saveToRoom(histories: List<History>) {
        try {
            histories.forEach { history ->
                roomRepository.insert(history)
            }
            Log.d("OfflineAwareHistoryRepo", "Cached ${histories.size} histories to Room")
        } catch (e: Exception) {
            Log.e("OfflineAwareHistoryRepo", "Error caching to Room", e)
        }
    }

    suspend fun syncPendingToFirebase() {
        if (!isOnline()) {
            Log.d("OfflineAwareHistoryRepo", "Cannot sync - offline")
            return
        }

        try {
            val unsyncedEntries = roomRepository.getUnsyncedEntries()
            Log.d("OfflineAwareHistoryRepo", "Found ${unsyncedEntries.size} unsynced entries to sync")

            unsyncedEntries.forEach { history ->
                try {
                    firebaseRepository.insert(history)
                    roomRepository.markAsSynced(history.id)
                    Log.d("OfflineAwareHistoryRepo", "Synced history to Firebase: ${history.id}")
                } catch (e: Exception) {
                    Log.e("OfflineAwareHistoryRepo", "Failed to sync history: ${history.id}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("OfflineAwareHistoryRepo", "Error syncing histories", e)
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

