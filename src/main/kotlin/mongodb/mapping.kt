package com.proschek.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.proschek.database
import com.proschek.model.ID
import com.proschek.model.Status
import com.proschek.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.getCollection
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.UUID

data class TodoDocument(
    // If your 'id' is stored as an ObjectId in MongoDB
    @BsonProperty("id") val id: ID = ID(UUID.randomUUID()),
    val title: String,
    val status: Status,
    @BsonId val _id: ObjectId = ObjectId()
) {
    // Add a no-args constructor for MongoDB
    constructor() : this(ID(), "", Status.TODO, ObjectId())
}

suspend fun <T> mongoOperation(operation: suspend (MongoCollection<TodoDocument>) -> T): T =
    withContext(Dispatchers.IO) {
        val collection = database.getCollection("todos", TodoDocument::class.java)
        operation(collection)
    }


fun documentToModel(doc: TodoDocument) = Todo(
    doc.id,
    doc.title,
    doc.status
)

fun modelToDocument(todo: Todo): TodoDocument = TodoDocument(
    id = todo.id,
    title = todo.title,
    status = todo.status
)