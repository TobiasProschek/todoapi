package com.proschek.routes

import com.proschek.model.CreateTodoRequest
import com.proschek.model.Status
import com.proschek.model.Todo
import com.proschek.repository.TodoRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.proschek.utils.toUUIDOrNull

fun Route.todoRoutes(todoRepository: TodoRepository) {
    route("/api/todos") {
    // GET /api/todos - Get all todos
        get {
            try {
                println("Starting to fetch todos...") // Add logging
                val todos = todoRepository.allTodos()
                println("Fetched ${todos.size} todos") // Add logging
                call.respond<List<Todo>>(HttpStatusCode.OK, todos)
            } catch (e: Exception) {
                println("Error occurred: ${e.message}") // Log the actual error
                e.printStackTrace() // Print full stack trace
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        // POST /api/todos - Create a new todo
        post {
            try {
                val request = call.receive<CreateTodoRequest>()
                if (request.title.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Title is required"))
                    return@post
                }
                if (request.status !in Status.entries) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Status must be valid"))
                    return@post
                }

                val todo = todoRepository.addTodo(request)
                call.respond(HttpStatusCode.Created, todo)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create todo"))
            }
        }

        // GET /api/todos/{id} - Get a specific todo
        get("/{id}") {
            val id = call.parameters["id"]
            val uuid = id.toUUIDOrNull()
            if (uuid == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@get
            }

            val todo = todoRepository.todoById(id.toString())
            if (todo != null) {
                call.respond(todo)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Todo not found"))
            }
        }

    // DELETE /api/todos/{id} - Delete a specific todo
        delete("/{id}") {
            val id = call.parameters["id"]
            val uuid = id.toUUIDOrNull()
            if (uuid == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@delete
            }

            val wasDeleted = todoRepository.removeTodo(id.toString())
            if (wasDeleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Todo not found"))
            }
        }

        // PUT /api/todos/{id} - Update a specific todo
        put("/{id}") {
            val id = call.parameters["id"]
            val uuid = id.toUUIDOrNull()

            if (uuid == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@put
            }
            val request = call.receive<Todo>()
            println("Received request: $request") // Debug log

            val updatedTodo = todoRepository.updateTodo(id.toString(), request)
            println("Updated todo result: $updatedTodo") // Debug log

            if (updatedTodo != null) {
                call.respond(updatedTodo)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Todo not found"))
            }
        }
    }
}