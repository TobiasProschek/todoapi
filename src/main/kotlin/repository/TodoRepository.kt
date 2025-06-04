package com.proschek.repository

import com.proschek.model.ID
import com.proschek.model.Todo

interface TodoRepository {
    suspend fun allTodos(): List<Todo>
    suspend fun todoById(id: ID): List<Todo>
    suspend fun addTodo(todo: Todo)
    suspend fun updateTodo(id: ID, todo: Todo)
    suspend fun removeTodo(id: ID): Boolean
}