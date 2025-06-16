package com.proschek.plugins

import com.proschek.routes.todoRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondText("Todo API is running!")
        }

        get("/health") {
            call.respondText("OK")
        }

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        todoRoutes()
    }
}