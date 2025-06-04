package com.proschek.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.proschek.config.database
import com.proschek.exception.TodoMongoException
import com.proschek.exception.TodoNotFoundException
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Todo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Repository interface for managing todo items with CRUD operations. */
interface ITodoRepository {
    /** Retrieves all todo items from the repository. */
    suspend fun allTodos(): List<Todo>

    /** Retrieves a todo item by ID */
    suspend fun todoById(id: String): Todo?

    /** Creates a new todo item in the repository. */
    suspend fun addTodo(request: CreateTodoRequest): Todo

    /** Updates an existing todo item by ID. */
    suspend fun updateTodo(
        id: String,
        todo: CreateTodoRequest,
    ): Todo?

    /** Removes a todo item by ID, returns true if successful or false if not found. */
    suspend fun removeTodo(id: String): Boolean
}

/** MongoDB implementation of the todo repository for data persistence. */
class TodoRepository(
    private val collectionName: String = "todos",
) : ITodoRepository {
    val collection: MongoCollection<Todo> by lazy {
        database.getCollection(collectionName, Todo::class.java)
    }

    override suspend fun allTodos(): List<Todo> =
        try {
            collection.find().toList()
        } catch (e: MongoException) {
            throw TodoMongoException("Failed to get data from the database: ${e.message}", e)
        }

    override suspend fun todoById(id: String): Todo? =
        try {
            collection.find(Filters.eq("id", id)).firstOrNull()
        } catch (e: MongoException) {
            throw TodoMongoException("Failed to get todo using id from database: ${e.message}", e)
        }

    override suspend fun addTodo(request: CreateTodoRequest): Todo =
        try {
            val todo = Todo.create(request.title, request.description, request.status)
            collection.insertOne(todo)
            todo
        } catch (e: MongoException) {
            throw TodoMongoException("Failed to add Todo to database: ${e.message}", e)
        }

    override suspend fun updateTodo(
        id: String,
        todo: CreateTodoRequest,
    ): Todo? =
        try {
            val result =
                collection.findOneAndUpdate(
                    Filters.eq("id", id),
                    Updates.combine(
                        Updates.set("title", todo.title),
                        Updates.set("description", todo.description),
                        Updates.set("status", todo.status),
                        Updates.set("updatedAt", Clock.System.todayIn(TimeZone.UTC)),
                    ),
                    FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER),
                )
            result ?: throw TodoNotFoundException("Todo not Found")
        } catch (e: MongoException) {
            throw TodoMongoException("Failed to update Todo in the database: ${e.message}", e)
        }

    override suspend fun removeTodo(id: String): Boolean =
        try {
            val result = collection.deleteOne(Filters.eq("id", id))
            result.deletedCount == 1L
        } catch (e: MongoException) {
            throw TodoMongoException("Failed to remove Todo from database: ${e.message}", e)
        }
}
