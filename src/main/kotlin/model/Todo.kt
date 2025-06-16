package com.proschek.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

enum class Status {
    TODO, IN_PROGRESS, DONE
}

@Serializable
data class Todo(
    @Contextual
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val status: Status
) {
    companion object {
        fun create(title: String, status: Status): Todo {
            return Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                status = status
            )
        }
    }
}

@Serializable
data class CreateTodoRequest(
    val title: String,
    val status: Status
)