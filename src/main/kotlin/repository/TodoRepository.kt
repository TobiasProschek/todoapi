package com.proschek.repository

import com.proschek.model.Todo
import java.util.UUID

interface TodoRepository {
    suspend fun allTodos(): List<Todo>
    suspend fun todoById(id: UUID): Todo?
    suspend fun addTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)
    suspend fun removeTodo(title: String): Boolean
}