package com.example.quizapp.ui.feature.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.TodoRepository
import com.example.quizapp.navigation.AddEditRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.UIEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val todos = repository.getAll(auth.currentUser?.uid ?: "")
        .catch {
            Log.e("ListViewModel", "Error getting todos", it)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ListEvent) {
        when(event) {
            is ListEvent.Delete -> {
                delete(event.id)
            }
            is ListEvent.Complete -> {
                complete(event.id, event.isCompleted)
            }
            is ListEvent.Edit -> {
                viewModelScope.launch {
                    _uiEvent.send(UIEvent.Navigate(AddEditRoute(event.id)))
                }
            }
            ListEvent.Logout -> {
                logout()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            auth.signOut()
            _uiEvent.send(UIEvent.Navigate(LoginRoute))
        }
    }

    private fun delete (id: String) {
        viewModelScope.launch {
            repository.delete(id, auth.currentUser?.uid ?: "")
        }
    }

    private fun complete (id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(id, isCompleted, auth.currentUser?.uid ?: "")
        }
    }
}