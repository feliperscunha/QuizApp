package com.example.quizapp.ui.feature.list

sealed interface ListEvent {
    data class Delete(val id: String) : ListEvent
    data class Complete(val id: String, val isCompleted: Boolean) : ListEvent
    data class Edit(val id: String?) : ListEvent
    data object Logout : ListEvent
}