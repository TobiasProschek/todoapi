package com.proschek.repository

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.proschek.config.database
import com.proschek.model.CreateTodoRequest
import com.proschek.model.Status
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

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

        val mongoClient = MongoClient.create(uri)
        database = mongoClient.getDatabase(databaseName) // Initialize the global variable
    }

    @Test
    fun `should create todo successfully and find todo by id`() =
        runTest {
            // Given
            val todo = createTodoRequest()

            // When
            val createdTodo = mongoTodoRepository.addTodo(todo)
            val foundTodo = mongoTodoRepository.todoById(createdTodo.id.toString())

            // Then
            assertThat(createdTodo).isNotNull()
            assertThat(createdTodo.id).isNotNull()
            assertThat(createdTodo.title).isEqualTo("Test Todo")
            assertThat(foundTodo).isEqualTo(createdTodo)
        }

    @Test
    fun `should find all todos`() =
        runTest {
            // Given
            val todoOne = mongoTodoRepository.addTodo(createTodoRequest())
            val todoTwo = mongoTodoRepository.addTodo(createTodoRequest("Todo 2"))

            // When
            val foundTodos = mongoTodoRepository.allTodos()

            // Then
            assertThat(foundTodos)
                .hasSize(2)
                .contains(todoOne, todoTwo)
        }

    @Test
    fun `should find todo by id`() =
        runTest {
            // Given
            val todoOne = mongoTodoRepository.addTodo(createTodoRequest())
            val todoTwo = mongoTodoRepository.addTodo(createTodoRequest("Test Todo"))

            // When & Then
            assertThat(mongoTodoRepository.todoById(todoOne.id.toString())).isEqualTo(todoOne)
            assertThat(mongoTodoRepository.todoById(todoTwo.id.toString())).isEqualTo(todoTwo)
            assertThat(todoOne).isNotEqualTo(todoTwo)
        }

    @Test
    fun `should update todo successfully`() =
        runTest {
            // Given
            val originalTodo =
                mongoTodoRepository.addTodo(
                    createTodoRequest("Original title", "Test description", Status.TODO),
                )
            val updateRequest = createTodoRequest("Updated title", "Test description", Status.DONE)

            // When
            val updatedTodo = mongoTodoRepository.updateTodo(originalTodo.id.toString(), updateRequest)

            // Then
            assertThat(updatedTodo).isNotNull()
            assertThat(updatedTodo!!.title).isEqualTo("Updated title")
            assertThat(updatedTodo.id).isEqualTo(originalTodo.id)
            assertThat(updatedTodo.status).isEqualTo(Status.DONE)
            assertThat(updatedTodo).isNotEqualTo(originalTodo)
        }

    @Test
    fun `should delete todo successfully`() =
        runTest {
            // Given
            val todo = mongoTodoRepository.addTodo(createTodoRequest())

            // When
            val isDeleted = mongoTodoRepository.removeTodo(todo.id.toString())
            val foundTodo = mongoTodoRepository.todoById(todo.id.toString())

            // Then
            assertThat(isDeleted).isTrue()
            assertThat(foundTodo).isNull()
        }
}
