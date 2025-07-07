package com.proschek.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import com.proschek.exception.ErrorResponse
import com.proschek.exception.TodoNotFoundException
import com.proschek.exception.TodoInvalidDataException
import com.proschek.exception.TodoAlreadyExistsException
import io.ktor.server.plugins.BadRequestException
import com.proschek.exception.TodoMongoException
import com.proschek.exception.TodoUUIDCreationException
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
                    ErrorResponse("Status must be one of: TODO, IN_PROGRESS, DONE", status = HttpStatusCode.BadRequest.value)
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request format", status = HttpStatusCode.BadRequest.value)
                )
            }
        }

        exception<TodoNotFoundException> { call, throwable ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.NotFound.value)
            )
        }

        exception<TodoInvalidDataException> { call, throwable ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.BadRequest.value)
            )
        }

        exception<TodoAlreadyExistsException> { call, throwable ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.Conflict.value)
            )
        }
        exception<TodoMongoException> { call, throwable ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.InternalServerError.value)
            )
        }
        exception<TodoUUIDCreationException> { call, throwable ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.InternalServerError.value)
            )
        }
        exception<Exception> { call, throwable ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "An unexpected error occurred", status = HttpStatusCode.InternalServerError.value)
            )
        }
    }
}