package com.proschek

import com.proschek.model.ID
import com.proschek.model.Todo
import com.proschek.repository.TodoRepository
import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: TodoRepository) {
    install(ContentNegotiation) {
        json()
    }
    routing{
        route("/todos") {
            get {
                val todo = repository.allTodos()
                call.respond(todo)
            }

            get("/byID/{id}") {
                val idString = call.parameters["id"]
                if(idString == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val id = ID.fromString(idString)
                    val todo = repository.todoById(id)
                    if (todo == null || todo.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(todo)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                }
            }


            post {
                try {
                    val todo = call.receive<Todo>()
                    repository.addTodo(todo)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            put("/{id}") {
                try {
                    val id: ID = try {
                        ID.fromString(call.parameters["id"] ?: throw IllegalStateException("Missing ID parameter"))
                    } catch (e: IllegalArgumentException) {
                        throw IllegalStateException("Invalid ID format")
                    }
                    val todo = call.receive<Todo>()

                    repository.updateTodo(id, todo)
                    call.respond(HttpStatusCode.NoContent)                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                // Get the ID from the correct parameter name
                val idString = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

                try {
                    // Convert string to ID (this won't return null)
                    val id = ID.fromString(idString)

                    // Try to remove the todo
                    if (repository.removeTodo(id)) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } catch (e: IllegalArgumentException) {
                    // This catches invalid UUID format exceptions
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                }
            }
        }
    }
}
