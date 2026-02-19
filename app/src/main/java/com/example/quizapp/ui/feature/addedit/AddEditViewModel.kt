package com.example.quizapp.ui.feature.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.auth.FirebaseAuthRepository
import com.example.quizapp.data.TodoRepository
import com.example.quizapp.ui.UIEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditViewModel (
    private val id: String? = null,
    private val repository: TodoRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<UIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        id?.let {
            viewModelScope.launch {
                val userId = authRepository.getCurrentUser()?.uid
                if (userId != null) {
                    val todo = repository.getBy(it, userId)
                    title = todo?.title ?: ""
                    description = todo?.description
                }
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.TitleChanged -> {
                title = event.title
            }
            is AddEditEvent.DescriptionChanged -> {
                description = event.description
            }

            AddEditEvent.Save -> {
                saveTodo()
            }
            AddEditEvent.NavigateBack -> {
                viewModelScope.launch {
                    _uiEvent.send(UIEvent.NavigateBack)
                }
            }
        }
    }

    private fun saveTodo () {
        viewModelScope.launch {
            if (title.isBlank()) {
                _uiEvent.send(UIEvent.ShowSnackBar(
                    message = "The title can't be empty"
                ))
                return@launch
            }
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                repository.insert(title, description, id, userId)
            }
            _uiEvent.send(UIEvent.NavigateBack)

        }
    }
}