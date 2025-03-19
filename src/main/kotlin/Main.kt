import org.openapitools.client.infrastructure.*
import org.openapitools.client.models.*
import org.openapitools.client.apis.TodoApi

fun main() {
    val apiInstance = TodoApi()
    val taskID: kotlin.Int = 56 // kotlin.Int | ToDo id to delete
    try {
        apiInstance.deleteToDo(taskID)
    } catch (e: ClientException) {
        println("4xx response calling TodoApi#deleteToDo")
        e.printStackTrace()
    } catch (e: ServerException) {
        println("5xx response calling TodoApi#deleteToDo")
        e.printStackTrace()
    }
}
