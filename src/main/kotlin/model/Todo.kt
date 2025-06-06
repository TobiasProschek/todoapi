package com.proschek.model

import kotlinx.serialization.Serializable
import java.util.UUID

enum class Status {
    TODO, IN_PROGRESS, DONE
}

@Serializable
data class Todo(
    val id: Int,
    val title: String,
    val status: Status
)