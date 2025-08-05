package com.proschek.repository

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.proschek.config.database
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Status
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TodoRepositoryTest {
    private lateinit var todoRepository: TodoRepository

    @BeforeTest
    fun setup() {
        // Initialize the database the same way your app does
        initializeDatabaseForTests()

        todoRepository = TodoRepository("todos-test")
    }

    @AfterTest
    fun cleanup() =
        runTest {
            todoRepository.collection.drop()
        }

    // for add new todos
    private fun createTodoRequest(
        title: String = "Test Todo",
        description: String = "Test Description",
        status: Status = Status.TODO,
    ): CreateTodoRequest =
        CreateTodoRequest(
            title = title,
            description = description,
            status = status,
        )

    private fun initializeDatabaseForTests() {
        val isCI = System.getenv("CI") == "true" || System.getenv("GITHUB_ACTIONS") == "true"
        val config = if (isCI) ConfigFactory.load("application-ci.conf") else ConfigFactory.load("application.conf")
        val uri = config.getConfig("ktor").getConfig("mongodb").getString("connectionString")
        val databaseName = config.getConfig("ktor").getConfig("mongodb").getString("databaseName")

        val mongoClient = MongoClient.Factory.create(uri)
        database = mongoClient.getDatabase(databaseName) // Initialize the global variable
    }

    @Test
    fun `should create todo successfully and find todo by id`() =
        runTest {
            val todo = createTodoRequest()
            // When - Create the Todo
            val createdTodo = todoRepository.addTodo(todo)

            // Then - Verify creation
            assertNotNull(createdTodo)
            assertNotNull(createdTodo.id)
            assertEquals("Test Todo", createdTodo.title)

            // When - Find the created Todo
            val foundTodo = todoRepository.todoById(createdTodo.id)

            // Then - Verify retrieval
            assertNotNull(foundTodo)
            assertEquals(createdTodo.id, foundTodo.id)
            assertEquals(createdTodo.title, foundTodo.title)
        }

    @Test
    fun `should find all todos`() =
        runTest {
            val todo = createTodoRequest()
            val createTodoOnes = todoRepository.addTodo(todo)
            val createTodoTwice = todoRepository.addTodo(createTodoRequest("Todo 2"))

            val foundTodos = todoRepository.allTodos()
            val filterTodoOne = foundTodos.filter { it.id == createTodoOnes.id }
            val filterTodoTwo = foundTodos.filter { it.id == createTodoTwice.id }

            assertEquals(filterTodoOne[0], createTodoOnes)
            assertEquals(filterTodoTwo[0], createTodoTwice)
            assertEquals(foundTodos.size, 2)
        }

    @Test
    fun `should find todo by id`() =
        runTest {
            val todo = createTodoRequest()
            val createTodoOne = todoRepository.addTodo(todo)
            val createTodoTwo = todoRepository.addTodo(createTodoRequest("Test Todo"))

            assertEquals(todoRepository.todoById(createTodoOne.id.toString()), createTodoOne)
            assertEquals(todoRepository.todoById(createTodoTwo.id.toString()), createTodoTwo)
            assertNotEquals(todoRepository.todoById(createTodoOne.id.toString()), createTodoTwo)
        }

    @Test
    fun `should update todo successfully`() =
        runTest {
            val createTodo =
                todoRepository.addTodo(createTodoRequest("Original title", "Test description", Status.TODO))
            val updateTodoRequest = createTodoRequest("Updated title", "Test description", Status.DONE)

            val updatedTodo = todoRepository.updateTodo(createTodo.id.toString(), updateTodoRequest)

            assertEquals(updatedTodo?.title, "Updated title")
            assertEquals(updatedTodo?.id, createTodo.id)
            assertNotEquals(updatedTodo?.status, createTodo.status)
            assertNotEquals(updatedTodo, createTodo)
        }

    @Test
    fun `should delete todo successfully`() =
        runTest {
            val createTodo = todoRepository.addTodo(createTodoRequest())

            val deleteTodo = todoRepository.removeTodo(createTodo.id.toString())
            val foundTodo = todoRepository.todoById(createTodo.id.toString())
            assertNull(foundTodo)
            assertTrue(deleteTodo)
        }
}
