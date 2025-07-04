package com.proschek.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import com.proschek.exception.ErrorResponse
import com.proschek.exception.TodoNotFoundException
import com.proschek.exception.InvalidTodoDataException
import com.proschek.exception.TodoAlreadyExistsException
//import com.proschek.exception.BadRequestException
import io.ktor.server.plugins.BadRequestException
import com.proschek.exception.TodoMongoException
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, throwable ->
            if (throwable.message?.contains("CreateTodoRequest") == true ||
                throwable.message?.contains("Status") == true ||
                throwable.message?.contains("enum") == true) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Status must be one of: TODO, IN_PROGRESS, DONE", status = 400)
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request format", status = 400)
                )
            }
        }

        exception<TodoNotFoundException> { call, throwable ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("${throwable.message}", status = HttpStatusCode.NotFound.value)
            )
        }

        exception<InvalidTodoDataException> { call, throwable ->
            println("🎯 InvalidTodoDataException caught: ${throwable.message}")
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("${throwable.message}", status = HttpStatusCode.BadRequest.value)
            )
        }

        exception<TodoAlreadyExistsException> { call, throwable ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("${throwable.message}", status = HttpStatusCode.Conflict.value)
            )
        }
        exception<TodoMongoException> { call, throwable ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("${throwable.message}", status = HttpStatusCode.InternalServerError.value)
            )
        }
        exception<Exception> { call, throwable ->
            println("🔍 CAUGHT Generic Exception:")
            println("   Type: ${throwable::class.simpleName}")
            println("   Full class: ${throwable::class.qualifiedName}")
            println("   Message: ${throwable.message}")
            println("   Is InvalidTodoDataException? ${throwable is InvalidTodoDataException}")
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "An unexpected error occurred", status = HttpStatusCode.InternalServerError.value)
            )
        }
    }
}

