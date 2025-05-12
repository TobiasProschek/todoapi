package com.proschek

import com.mongodb.client.*
import com.proschek.repository.TodoRepository
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: TodoRepository) {
    install(ContentNegotiation) {
        json()
    }
    routing{
        route("/tasks") {
            get {
                val todo = repository.allTodos()
                call.respond(todo)
            }

            get("/byID/{id}") {
                val id = call.parameters["id"]
                if(id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
            }
        }
    }
}
