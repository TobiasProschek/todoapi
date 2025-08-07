package com.proschek.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

/** Configures JSON serialization with explicit nulls disabled. */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false
            },
        )
    }
}
