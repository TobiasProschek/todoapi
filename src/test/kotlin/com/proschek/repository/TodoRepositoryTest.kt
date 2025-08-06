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
    private lateinit var mongoTodoRepository: MongoTodoRepository

    @BeforeTest
    fun setup() {
        // Initialize the database the same way your app does
        initializeDatabaseForTests()

        mongoTodoRepository = MongoTodoRepository("todos-test")
    }

    @AfterTest
    fun cleanup() =
        runTest {
            mongoTodoRepository.collection.drop()
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
            val createdTodo = mongoTodoRepository.addTodo(todo)

            // Then - Verify creation
            assertNotNull(createdTodo)
            assertNotNull(createdTodo.id)
            assertEquals("Test Todo", createdTodo.title)

            // When - Find the created Todo
            val foundTodo = mongoTodoRepository.todoById(createdTodo.id)

            // Then - Verify retrieval
            assertNotNull(foundTodo)
            assertEquals(createdTodo.id, foundTodo.id)
            assertEquals(createdTodo.title, foundTodo.title)
        }

    @Test
    fun `should find all todos`() =
        runTest {
            val todo = createTodoRequest()
            val createTodoOnes = mongoTodoRepository.addTodo(todo)
            val createTodoTwice = mongoTodoRepository.addTodo(createTodoRequest("Todo 2"))

            val foundTodos = mongoTodoRepository.allTodos()
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
            val createTodoOne = mongoTodoRepository.addTodo(todo)
            val createTodoTwo = mongoTodoRepository.addTodo(createTodoRequest("Test Todo"))

            assertEquals(mongoTodoRepository.todoById(createTodoOne.id.toString()), createTodoOne)
            assertEquals(mongoTodoRepository.todoById(createTodoTwo.id.toString()), createTodoTwo)
            assertNotEquals(mongoTodoRepository.todoById(createTodoOne.id.toString()), createTodoTwo)
        }

    @Test
    fun `should update todo successfully`() =
        runTest {
            val createTodo =
                mongoTodoRepository.addTodo(createTodoRequest("Original title", "Test description", Status.TODO))
            val updateTodoRequest = createTodoRequest("Updated title", "Test description", Status.DONE)

            val updatedTodo = mongoTodoRepository.updateTodo(createTodo.id.toString(), updateTodoRequest)

            assertEquals(updatedTodo?.title, "Updated title")
            assertEquals(updatedTodo?.id, createTodo.id)
            assertNotEquals(updatedTodo?.status, createTodo.status)
            assertNotEquals(updatedTodo, createTodo)
        }

    @Test
    fun `should delete todo successfully`() =
        runTest {
            val createTodo = mongoTodoRepository.addTodo(createTodoRequest())

            val deleteTodo = mongoTodoRepository.removeTodo(createTodo.id.toString())
            val foundTodo = mongoTodoRepository.todoById(createTodo.id.toString())
            assertNull(foundTodo)
            assertTrue(deleteTodo)
        }
}
