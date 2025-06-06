package com.proschek.repository

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.proschek.collection
import com.proschek.model.Todo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

interface ITodoRepository {
    suspend fun allTodos(): List<Todo>
    suspend fun todoById(id: Int): Todo?
    suspend fun addTodo(todo: Todo): Todo
    suspend fun updateTodo(id: Int, todo: Todo): Todo?
    suspend fun removeTodo(id: Int): Boolean
}

class TodoRepository: ITodoRepository  {
    override suspend fun allTodos(): List<Todo> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }

    override suspend fun todoById(id: Int): Todo? {
        return try {
            collection.find(Filters.eq("id", id)).firstOrNull()
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }

    override suspend fun addTodo(todo: Todo): Todo {
        collection.insertOne(todo)
        return todo
    }

    override suspend fun updateTodo(id: Int, todo: Todo): Todo? {
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


    override suspend fun removeTodo(id: Int): Boolean {
        return try {
            val result = collection.deleteOne(Filters.eq("id", id))
            result.deletedCount == 1L
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            throw e
        }
    }
}