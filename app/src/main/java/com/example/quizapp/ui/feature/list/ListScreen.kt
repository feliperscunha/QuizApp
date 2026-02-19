package com.example.quizapp.ui.feature.list

import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapp.ui.theme.QuizAppTheme
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.TodoRepositoryImpl
import com.example.quizapp.domain.Todo
import com.example.quizapp.domain.todo1
import com.example.quizapp.domain.todo2
import com.example.quizapp.domain.todo3
import com.example.quizapp.navigation.AddEditRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.UIEvent
import com.example.quizapp.ui.components.TodoItem
import com.google.firebase.database.FirebaseDatabase


@Composable
fun ListScreen(
    navigateToAddEditScreen: (id: String?) -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    val repository = TodoRepositoryImpl(
        db = FirebaseDatabase.getInstance()
    )
    val viewModel = viewModel<ListViewModel> {
        ListViewModel(repository = repository)
    }

    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UIEvent.ShowSnackBar -> {
                }
                UIEvent.NavigateBack -> {
                }
                is UIEvent.Navigate<*> -> {
                    when(event.route) {
                        is AddEditRoute -> {
                            navigateToAddEditScreen(event.route.id)
                        }
                        is LoginRoute -> {
                            navigateToLoginScreen()
                        }
                    }
                }
            }
        }
    }

    ListContent(
        todos = todos,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContent(
    todos: List<Todo>,
    onEvent: (ListEvent) -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Todo List") },
                actions = {
                    TextButton(onClick = { onEvent(ListEvent.Logout) }) {
                        Text("sign out", color = Color.Black)
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Black,
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ListEvent.Edit(null))
                },
                containerColor = Color.Black,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (todos.isEmpty()) {
                item { Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "😄",
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tudo limpo!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adicione uma tarefa no botão +",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } }
            } else {
                itemsIndexed(todos) { index, todo ->
                    TodoItem(
                        todo = todo,
                        onCompletedChange = {
                            onEvent(ListEvent.Complete(todo.id, it))
                        },
                        onItemClick = {
                            onEvent(ListEvent.Edit(todo.id))
                        },
                        onDeleteClick = {
                            onEvent(ListEvent.Delete(todo.id))
                        }
                    )

                    if (index < todos.lastIndex) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListContentPreview() {
    QuizAppTheme {
        ListContent(
            todos = listOf(
                todo1,
                todo2,
                todo3
            ),
            onEvent = {}
        )
    }
}