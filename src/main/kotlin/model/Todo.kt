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
    val description: String,
    val status: Status,
    val createdAt: LocalDate? = null,
    val updatedAt: LocalDate? = null
) {
    companion object {
        fun create(title: String, description: String, status: Status): Todo {
            return Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                status = status,
                createdAt = Clock.System.todayIn(TimeZone.UTC)
            )
        }
    }
}

fun createStatus(status: Status): Status {
    when(status){
        Status.TODO, Status.IN_PROGRESS -> {
            return status
        }
        Status.DONE -> {
            throw IllegalArgumentException("Cannot create Todo with DONE status")
        }
    }
}

@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String,
    val status: Status
)