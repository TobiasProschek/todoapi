import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

// Domain model example
data class ToDo(
    val id: Int,
    val name: String,
    val email: String,
    val priority: String,
    val status: String
)

@SpringBootApplication
class ToDoApplication

@Service
class ToDoService(private val mongoRepo: MongoRepo) {
    fun getTodoById(id: String): ToDo? {
        return mongoRepo.findById(id).orElse(null)
    }
}

fun main(args: Array<String>) {
// Start your Spring Boot application and retrieve the ApplicationContext
    val context: ApplicationContext = runApplication<ToDoApplication>(*args)

// Get the MongoRepo bean from the context
    val todoRepository: MongoRepo = context.getBean(MongoRepo::class.java)
    val toDo = ToDo(1, "John Doe", "john@example.com", "HIGH", "INPROGRESS")
    todoRepository.save(toDo)

// Get the ToDoService bean from the context
    val todoService: ToDoService = context.getBean(ToDoService::class.java)
    println("Fetched: " + todoService.getTodoById("1"))
}