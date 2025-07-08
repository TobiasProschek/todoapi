package com.proschek.exception

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val timestamp: String = Clock.System.now().toString(),
    val status: Int
)
