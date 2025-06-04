package com.proschek.model

import com.fasterxml.jackson.databind.ser.std.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

enum class Status {
    TODO, IN_PROGRESS, DONE
}

@Serializable
data class Todo(
    val id: ID = ID(),
    val title: String,
    val status: Status
)