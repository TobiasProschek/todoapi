package com.proschek.model

import com.proschek.exception.TodoInvalidDataException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import java.util.UUID

/** Represents the possible states of a todo item. */
enum class Status {
    TODO,
    IN_PROGRESS,
    DONE,
}

/** Todo item data model */
@Serializable
data class Todo(
    val id: String? = null,
    val title: String,
    val description: String,
    val status: Status,
    val createdAt: LocalDate,
    val updatedAt: LocalDate?,
) {
    companion object {
        /** Creates a new Todo with validation that prevents DONE status on creation. */
        fun create(
            title: String,
            description: String,
            status: Status,
        ): Todo {
            require(status != Status.DONE) { throw TodoInvalidDataException("Cannot create Todo with DONE status") }
            return Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                status = status,
                createdAt = Clock.System.todayIn(TimeZone.UTC),
                updatedAt = null,
            )
        }
    }
}

/** Request data for creating a new todo item. */
@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String,
    val status: Status,
)
