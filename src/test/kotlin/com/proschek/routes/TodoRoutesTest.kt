package com.proschek.routes

import com.proschek.exception.ErrorResponse
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Status
import com.proschek.model.Todo
import com.proschek.plugins.configureBadRequestException
import com.proschek.plugins.configureException
import com.proschek.plugins.configureTodoInvalidDataException
import com.proschek.plugins.configureTodoMongoException
import com.proschek.plugins.configureTodoNotFoundException
import com.proschek.repository.TodoRepository
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TodoRoutesTest {
    private lateinit var mockkTodoRepository: TodoRepository
    private val logger = LoggerFactory.getLogger("StatusPagesTest")

    @BeforeTest
    fun setUp() {
        mockkTodoRepository = mockk<TodoRepository>()
        clearMocks(mockkTodoRepository)
    }

    @AfterTest
    fun tearDown() {
        clearMocks(mockkTodoRepository)
    }

    private fun ApplicationTestBuilder.setupTodoApp() {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            configureBadRequestException(logger)
            configureTodoNotFoundException(logger)
            configureTodoInvalidDataException(logger)
            configureTodoMongoException(logger)
            configureException(logger)
        }
        routing {
            todoRoutes(mockkTodoRepository)
        }
    }

    companion object {
        private fun createSampleTodo(id: String) =
            Todo(
                id = id,
                title = "Buy groceries",
                description = "Get milk and bread",
                status = Status.TODO,
                createdAt = LocalDate(2025, 8, 24),
                updatedAt = null,
            )
    }

    @Test
    fun `GET api todos should return all todos`() =
        testApplication {
            val expectedTodos =
                listOf(
                    createSampleTodo("cd9ee099-988e-45c0-98e1-9d2b90e1a8da"),
                )

            coEvery { mockkTodoRepository.allTodos() } returns expectedTodos

            setupTodoApp()

            val response = client.get("/api/todos")

            assertEquals(HttpStatusCode.OK, response.status)
            val actualTodos = Json.decodeFromString<List<Todo>>(response.bodyAsText())
            assertEquals(expectedTodos, actualTodos)

            coVerify { mockkTodoRepository.allTodos() }
        }

    @Test
    fun `GET api Todo return by ID`() =
        testApplication {
            val expectedTodos: Todo? = createSampleTodo("cd9ee099-988e-45c0-98e1-9d2b90e1a8da")

            coEvery { mockkTodoRepository.todoById("cd9ee099-988e-45c0-98e1-9d2b90e1a8da") } returns expectedTodos

            setupTodoApp()

            val response = client.get("/api/todos/cd9ee099-988e-45c0-98e1-9d2b90e1a8da")

            assertEquals(HttpStatusCode.OK, response.status)

            val jsonString = response.bodyAsText()
            val actualTodo: Todo = Json.decodeFromString(jsonString)
            assertEquals(expectedTodos, actualTodo)

            coVerify { mockkTodoRepository.todoById("cd9ee099-988e-45c0-98e1-9d2b90e1a8da") }
        }

    @Test
    fun `post api to create a TODO`() =
        testApplication {
            val newTestTodoRequest =
                CreateTodoRequest(
                    title = "Test Creation",
                    description = "Test Description for Test Todo",
                    status = Status.TODO,
                )

            val createdTodo = createSampleTodo("cd9ee099-988e-45c0-98e1-9d2b90e1a8da")

            coEvery { mockkTodoRepository.addTodo(newTestTodoRequest) } returns createdTodo

            setupTodoApp()

            val response =
                client.post("/api/todos") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(newTestTodoRequest))
                }

            assertEquals(HttpStatusCode.Created, response.status)
            val actualTodo: Todo = Json.decodeFromString(response.bodyAsText())
            assertEquals(createdTodo, actualTodo)

            coVerify { mockkTodoRepository.addTodo(newTestTodoRequest) }
        }

    @Test
    fun `delete api to remove a TODO by valid ID`() =
        testApplication {
            val todoId = "550e8400-e29b-41d4-a716-446655440000"

            coEvery { mockkTodoRepository.removeTodo(todoId) } returns true

            setupTodoApp()

            val response = client.delete("/api/todos/$todoId")

            assertEquals(HttpStatusCode.NoContent, response.status)
            coVerify { mockkTodoRepository.removeTodo(todoId) }
        }

    @Test
    fun `put api to update a TODO by valid ID`() =
        testApplication {
            val todoId = "550e8400-e29b-41d4-a716-446655440000"
            val updateRequest =
                CreateTodoRequest(
                    title = "Updated Title",
                    description = "Updated Description",
                    status = Status.IN_PROGRESS,
                )

            val updatedTodo =
                Todo(
                    id = todoId,
                    title = updateRequest.title,
                    description = updateRequest.description,
                    status = updateRequest.status,
                    createdAt = Clock.System.todayIn(TimeZone.UTC),
                    updatedAt = Clock.System.todayIn(TimeZone.UTC),
                )

            coEvery { mockkTodoRepository.updateTodo(todoId, updateRequest) } returns updatedTodo

            setupTodoApp()

            val response =
                client.put("/api/todos/$todoId") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(updateRequest))
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val actualTodo: Todo = Json.decodeFromString(response.bodyAsText())
            assertEquals(updatedTodo, actualTodo)

            coVerify { mockkTodoRepository.updateTodo(todoId, updateRequest) }
        }

    @Test
    fun `GET api todo by invalid ID format should return 400`() =
        testApplication {
            setupTodoApp()

            val response = client.get("/api/todos/invalid-id-format")

            assertEquals(HttpStatusCode.BadRequest, response.status)

            val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
            assertTrue(errorResponse.message.contains("Invalid ID", ignoreCase = true))
            assertEquals(400, errorResponse.status)
        }

    @Test
    fun `GET api todo by valid but non-existent ID should return 404`() =
        testApplication {
            val nonExistentId = "550e8400-e29b-41d4-a716-446655440999"

            coEvery { mockkTodoRepository.todoById(nonExistentId) } returns null

            setupTodoApp()

            val response = client.get("/api/todos/$nonExistentId")

            assertEquals(HttpStatusCode.NotFound, response.status)

            val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
            assertTrue(errorResponse.message.contains("not found", ignoreCase = true))
            assertEquals(404, errorResponse.status)

            coVerify { mockkTodoRepository.todoById(nonExistentId) }
        }

    @Test
    fun `POST api todos with invalid JSON should return 400`() =
        testApplication {
            setupTodoApp()

            val response =
                client.post("/api/todos") {
                    contentType(ContentType.Application.Json)
                    setBody("{ invalid json }")
                }

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
}
