package com.proschek

import com.proschek.config.configureDatabases
import com.proschek.plugins.configureRouting
import com.proschek.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
