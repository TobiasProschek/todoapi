package com.proschek.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.proschek.config.collection
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Todo
import kotlinx.coroutines.flow.first
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
            println("Failed to get todo from databse: ${e.message}")
            throw e
        }
    }

    override suspend fun todoById(id: String): Todo? {
        return try {
            collection.find(Filters.eq("id", id)).firstOrNull()
        } catch (e: Exception) {
            println("Failed to get todo using id from database: ${e.message}")
            throw e
        }
    }

    override suspend fun addTodo(request: CreateTodoRequest): Todo {
        return try {
            val todo = Todo.create(request.title, request.status)
            collection.insertOne(todo)
            todo
        } catch (e: Exception) {
            println("Failed to add todo to database: ${e.message}")
            throw e
        }
    }

    override suspend fun updateTodo(id: String, todo: Todo): Todo? {
        return try {
            val result = collection.updateOne(
                Filters.eq("id", id),
                Updates.combine(
                    Updates.set("title", todo.title),
                    Updates.set("status", todo.status)
                )
            )
            if (result.modifiedCount == 1L) {
                todo.copy(id = id)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Failed to update todo to database: ${e.message}")
            throw e
        }
    }


    override suspend fun removeTodo(id: String): Boolean {
        return try {
            val result = collection.deleteOne(Filters.eq("id", id))
            result.deletedCount == 1L
        } catch (e: Exception) {
            println("Failed to remove todo from database: ${e.message}")
            throw e
        }
    }
}

suspend fun clearAllTodos() {
    collection.deleteMany(Filters.empty())
}