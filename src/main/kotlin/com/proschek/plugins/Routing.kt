package com.proschek.plugins

import com.proschek.repository.TodoRepository
import com.proschek.routes.todoRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/** Configures application routing with basic health check endpoint. */
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Todo API is running!")
        }

        get("/health") {
            call.respondText("OK")
        }

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        todoRoutes(
            todoRepository = TodoRepository(),
        )
    }
}
