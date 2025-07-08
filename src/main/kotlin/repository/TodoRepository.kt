package com.proschek.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import com.proschek.config.collection
import com.proschek.exception.TodoMongoException
import com.proschek.exception.TodoNotFoundException
import com.proschek.model.CreateTodoRequest
import com.proschek.model.createStatus
import com.proschek.model.Todo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

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
            throw TodoMongoException("Failed to get data from the database: ${e.message}", e)
        }
    }

    override suspend fun todoById(id: String): Todo? {
        return try {
            collection.find(Filters.eq("id", id)).firstOrNull()
        } catch (e: Exception) {
            throw TodoMongoException("Failed to get todo using id from database: ${e.message}", e)
        }
    }

    override suspend fun addTodo(request: CreateTodoRequest): Todo {
        return try {
            val todo = Todo.create(request.title, request.description,createStatus(request.status))
            collection.insertOne(todo)
            todo
        } catch (e: Exception) {
            throw TodoMongoException("Failed to add Todo to database: ${e.message}", e)
        }
    }

    override suspend fun updateTodo(id: String, todo: Todo): Todo? {
        return try {
            val result = collection.findOneAndUpdate(
                Filters.eq("id", id),
                Updates.combine(
                    Updates.set("title", todo.title),
                    Updates.set("description", todo.description),
                    Updates.set("status", todo.status),
                    Updates.set("updatedAt", Clock.System.todayIn(TimeZone.UTC))
                )
                        FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
            )
                result ?: throw TodoNotFoundException("Todo not Found")
        } catch (e: Exception) {
            throw TodoMongoException("Failed to update Todo in the database: ${e.message}", e)
        }
    }

    override suspend fun removeTodo(id: String): Boolean {
        return try {
            val result = collection.deleteOne(Filters.eq("id", id))
            result.deletedCount == 1L
        } catch (e: Exception) {
            throw TodoMongoException("Failed to remove Todo from database: ${e.message}", e)
        }
    }
}