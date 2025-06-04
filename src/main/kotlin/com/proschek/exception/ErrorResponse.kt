package com.proschek.exception

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/** Represents a standardized error response with message, timestamp, and HTTP status code. */
@Serializable
data class ErrorResponse(
    val message: String,
    val timestamp: Instant = Clock.System.now(),
    val status: Int,
)
