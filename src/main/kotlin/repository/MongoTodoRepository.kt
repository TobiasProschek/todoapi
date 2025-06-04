package com.proschek.repository

import com.mongodb.client.model.Filters
import com.proschek.model.ID
import com.proschek.model.Todo
import com.proschek.mongodb.TodoDocument
import com.proschek.mongodb.documentToModel
import com.proschek.mongodb.modelToDocument
import com.proschek.mongodb.mongoOperation
import org.litote.kmongo.eq
import org.litote.kmongo.insertOne

class MongoTodoRepository: TodoRepository {
    override suspend fun allTodos(): List<Todo> = mongoOperation { collection ->
        val documents = collection.find().into(ArrayList())
        documents.map { documentToModel(it) }
    }

    override suspend fun todoById(id: ID): List<Todo> = mongoOperation { collection ->
        collection.find(Filters.eq("id", id.value))
            .into(ArrayList())
            .map { documentToModel(it) }
    }

    override suspend fun addTodo(todo: Todo) {
        insertOne(modelToDocument(todo))
    }

    override suspend fun updateTodo(id: ID, todo: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTodo(id: ID): Boolean = mongoOperation { collection ->
        val result = collection.deleteOne(Filters.eq("id", id.value))
        result.deletedCount == 1L
    }
}