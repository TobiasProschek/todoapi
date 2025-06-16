package com.proschek.repository

import com.mongodb.client.model.Filters
import com.proschek.config.collection
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Todo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface ITodoRepository {
    suspend fun allTodos(): List<Todo>
    suspend fun todoById(id: String): Todo?
    suspend fun addTodo(request: CreateTodoRequest): Todo
    suspend fun updateTodo(id: String, todo: Todo): Todo?
    suspend fun removeTodo(id: String): Boolean
}

class TodoRepository : ITodoRepository {
    override suspend fun allTodos(): List<Todo> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }

    override suspend fun todoById(id: String): Todo? {
        return try {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }

    // Update your repository interface/implementation
    override suspend fun addTodo(request: CreateTodoRequest): Todo {
        val todo = Todo.create(request.title, request.status)
        collection.insertOne(todo)
        return todo
    }

    override suspend fun updateTodo(id: String, todo: Todo): Todo? {
        return try {
            val result = collection.replaceOne(
                Filters.eq("id", id),
                todo
            )
            if (result.modifiedCount == 1L) {
                todo
            } else {
                null
            }
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }


    override suspend fun removeTodo(id: String): Boolean {
        return try {
            val result = collection.deleteOne(Filters.eq("id", id))
            result.deletedCount == 1L
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }
}

suspend fun clearAllTodos() {
    collection.deleteMany(Filters.empty())
}