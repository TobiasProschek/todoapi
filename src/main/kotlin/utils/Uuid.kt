package com.proschek.utils

import com.proschek.exception.TodoUUIDCreationException
import java.util.UUID

fun String?.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        throw TodoUUIDCreationException("Failed by Creating a UUID: ${e.message}")
    }
}