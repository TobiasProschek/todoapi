package com.proschek.plugins

import com.proschek.exception.ErrorResponse
import com.proschek.exception.TodoInvalidDataException
import com.proschek.exception.TodoMongoException
import com.proschek.exception.TodoNotFoundException
import com.proschek.model.Status
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.util.logging.Logger
import kotlinx.serialization.SerializationException
import org.slf4j.LoggerFactory

/** Configures global exception handling and status page responses. */
fun Application.configureStatusPages() {
    val logger = LoggerFactory.getLogger("StatusPages")

    install(StatusPages) {
        configureBadRequestException(logger)
        configureTodoNotFoundException(logger)
        configureTodoInvalidDataException(logger)
        configureTodoMongoException(logger)
        configureException(logger)
    }
}

/** Handles BadRequestException with appropriate error messages for malformed requests. */
fun StatusPagesConfig.configureBadRequestException(logger: Logger) {
    exception<BadRequestException> { call, throwable ->
        logger.warn(
            "Bad request - Method: {}, URI: {}, Message: {}",
            call.request.httpMethod.value,
            call.request.uri,
            throwable.message,
        )

        when (val cause = throwable.cause) {
            is SerializationException -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Invalid request format", status = HttpStatusCode.BadRequest.value),
                )
            }
            else -> {
                if (cause?.javaClass?.simpleName == "JsonConvertException") {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            "Status must be one of: ${Status.entries}",
                            status = HttpStatusCode.BadRequest.value,
                        ),
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Invalid request format", status = HttpStatusCode.BadRequest.value),
                    )
                }
            }
        }
    }
}

/** Handles TodoNotFoundException and returns 404 Not Found response. */
fun StatusPagesConfig.configureTodoNotFoundException(logger: Logger) {
    exception<TodoNotFoundException> { call, throwable ->
        logger.warn(
            "Todo not found - Method: {}, URI: {}, Message: {}",
            call.request.httpMethod.value,
            call.request.uri,
            throwable.message,
        )
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(throwable.message.toString(), status = HttpStatusCode.NotFound.value),
        )
    }
}

/** Handles TodoInvalidDataException for validation errors like invalid ID formats. */
fun StatusPagesConfig.configureTodoInvalidDataException(logger: Logger) {
    exception<TodoInvalidDataException> { call, throwable ->
        logger.warn(
            "Invalid todo data - Method: {}, URI: {}, Message: {}",
            call.request.httpMethod.value,
            call.request.uri,
            throwable.message,
        )
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(throwable.message.toString(), status = HttpStatusCode.BadRequest.value),
        )
    }
}

/** Handles TodoMongoException for database-related errors. */
fun StatusPagesConfig.configureTodoMongoException(logger: Logger) {
    exception<TodoMongoException> { call, throwable ->
        logger.error(
            "Todo database error - Method: {}, URI: {}, Message: {}",
            call.request.httpMethod.value,
            call.request.uri,
            throwable.message,
        )
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(throwable.message.toString(), status = HttpStatusCode.InternalServerError.value),
        )
    }
}

/** Handles unexpected exceptions with generic 500 Internal Server Error response. */
fun StatusPagesConfig.configureException(logger: Logger) {
    exception<Exception> { call, throwable ->
        logger.error(
            "Unexpected error - Method: {}, URI: {}, Message: {}",
            call.request.httpMethod.value,
            call.request.uri,
            throwable.message,
        )
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(message = "An unexpected error occurred", status = HttpStatusCode.InternalServerError.value),
        )
    }
}
