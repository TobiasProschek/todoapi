package com.proschek

import com.proschek.config.configureDatabases
import com.proschek.plugins.configureHTTP
import com.proschek.plugins.configureRouting
import com.proschek.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
