package com.example.quizapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizapp.domain.Todo
import com.example.quizapp.domain.todo1
import com.example.quizapp.domain.todo2
import com.example.quizapp.ui.theme.TodoListTheme

@Composable
fun TodoItem(
    todo: Todo,
    onCompletedChange: (Boolean) -> Unit,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedColor = Color(0xFF2E7D32)

    Surface(
        onClick = onItemClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 5.dp,
        border = BorderStroke(
            1.dp,
            color = if (todo.isCompleted) {
                completedColor
            } else {
                MaterialTheme.colorScheme.error
            }),
    ) {
        Row (
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = onCompletedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = completedColor,
                    checkmarkColor = Color.White,
                    uncheckedColor = MaterialTheme.colorScheme.error
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column (
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = if (todo.isCompleted) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else {
                        androidx.compose.ui.text.style.TextDecoration.None
                    }
                )

                todo.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}

@Preview
@Composable
private fun TodoItemPreview() {
    TodoListTheme {
        TodoItem(
            todo = todo1,
            onCompletedChange = {},
            onItemClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview
@Composable
private fun TodoItemCompletedPreview() {
    TodoListTheme {
        TodoItem(
            todo = todo2,
            onCompletedChange = {},
            onItemClick = {},
            onDeleteClick = {}
        )
    }
}