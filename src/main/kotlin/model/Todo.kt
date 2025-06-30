package com.proschek.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.UUID

enum class Status {
    TODO, IN_PROGRESS, DONE
}

@Serializable
data class Todo(
    val id: String? = null,
    val title: String,
    val status: Status,
    val createdAt: LocalDate? = null
) {
    companion object {
        fun create(title: String, status: Status): Todo {
            return Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                status = status,
                createdAt = Clock.System.todayIn(TimeZone.UTC)
            )
        }
    }
}

@Serializable
data class CreateTodoRequest(
    val title: String,
    val status: Status
)