import org.openapitools.client.infrastructure.*
import org.openapitools.client.models.*
import org.openapitools.client.apis.TodoApi
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

suspend fun main() {
//    val apiInstance = TodoApi()
//    val taskID: kotlin.Int = 56 // kotlin.Int | ToDo id to delete
//    try {
//        apiInstance.deleteToDo(taskID)
//    } catch (e: ClientException) {
//        println("4xx response calling TodoApi#deleteToDo")
//        e.printStackTrace()
//    } catch (e: ServerException) {
//        println("5xx response calling TodoApi#deleteToDo")
//        e.printStackTrace()
    val database = getDatabase()
    runBlocking {
        addToDo(database)
    }
}

fun getDatabase(): MongoDatabase {
    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
    return client.getDatabase(databaseName = "todoapi-tobi-test")
}

data class ToDoInfo(
    @BsonId
    val id: ObjectId,
    val title: String,
    val description: String,
    val priority: Int,
    val status: String,

    @BsonProperty("todo_id")
    val todoID: String
)

suspend fun addToDo(database: MongoDatabase) {

    val info = ToDoInfo(
        id = ObjectId(),
        title = "titleTest",
        description = "Blablabla",
        priority = 1,
        status = "WAITING",
        todoID = "randomID"
    )

    val collection = database.getCollection<ToDoInfo>(collectionName = "todos")
    collection.insertOne(info).also {
        println("Inserted ID " + it.insertedId)
    }
}