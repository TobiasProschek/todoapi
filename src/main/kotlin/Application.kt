package com.proschek

import com.proschek.config.configureDatabases
import com.proschek.plugins.configureHTTP
import com.proschek.plugins.configureRouting
import com.proschek.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.EngineMain
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        module()
    }.start(wait = true)
}

private val logger = LoggerFactory.getLogger("Application")

fun Application.module() {
    logger.info("Application module loaded")
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
