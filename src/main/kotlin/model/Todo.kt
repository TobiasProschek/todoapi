package com.proschek.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import java.util.UUID

enum class Status {
    TODO, IN_PROGRESS, DONE
}

@Serializable
data class Todo(
    val id: String? = null,
    val title: String,
    val description: String,
    val status: Status,
    val createdAt: LocalDate,
    val updatedAt: LocalDate?
) {
    companion object {
        fun create(title: String, description: String, status: Status): Todo {
            require(status != Status.DONE) { "Cannot create Todo with DONE status" }
            return Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                status = status,
                createdAt = Clock.System.todayIn(TimeZone.UTC),
                updatedAt = null
            )
        }
    }
}

@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String,
    val status: Status
)