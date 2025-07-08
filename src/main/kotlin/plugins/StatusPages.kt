package com.proschek.plugins

import com.proschek.exception.ErrorResponse
import com.proschek.exception.TodoNotFoundException
import com.proschek.exception.TodoInvalidDataException
import com.proschek.exception.TodoMongoException
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.request.httpMethod
import org.slf4j.LoggerFactory

fun Application.configureStatusPages() {

    val logger = LoggerFactory.getLogger("StatusPages")

    install(StatusPages) {
        exception<BadRequestException> { call, throwable ->
            logger.warn(
                "Bad request - Method: {}, URI: {}, Message: {}",
                call.request.httpMethod.value,
                call.request.uri,
                throwable.message
            )
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
            logger.warn(
                "Todo not found - Method: {}, URI: {}, Message: {}",
                call.request.httpMethod.value,
                call.request.uri,
                throwable.message
            )
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.NotFound.value)
            )
        }

        exception<TodoInvalidDataException> { call, throwable ->
            logger.warn(
                "Invalid todo data - Method: {}, URI: {}, Message: {}",
                call.request.httpMethod.value,
                call.request.uri,
                throwable.message
            )
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.BadRequest.value)
            )
        }

        exception<TodoMongoException> { call, throwable ->
            logger.error(
                "Todo database error - Method: {}, URI: {}, Message: {}",
                call.request.httpMethod.value,
                call.request.uri,
                throwable.message
            )
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(throwable.message.toString(), status = HttpStatusCode.InternalServerError.value)
            )
        }

        exception<Exception> { call, throwable ->
            logger.error(
                "Unexpected error - Method: {}, URI: {}, Message: {}",
                call.request.httpMethod.value,
                call.request.uri,
                throwable.message
            )
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(message = "An unexpected error occurred", status = HttpStatusCode.InternalServerError.value)
            )
        }
    }
}