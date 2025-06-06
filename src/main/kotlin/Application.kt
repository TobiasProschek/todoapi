package com.proschek

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import com.proschek.plugins.configureSerialization
import com.proschek.plugins.configureRouting

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
