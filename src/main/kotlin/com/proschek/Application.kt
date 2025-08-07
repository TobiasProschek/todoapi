package com.proschek

import com.proschek.config.configureDatabases
import com.proschek.plugins.configureHTTP
import com.proschek.plugins.configureRouting
import com.proschek.plugins.configureSerialization
import com.proschek.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

/** Application entry point that starts the Ktor server. */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

private val logger = LoggerFactory.getLogger("Application")

/** Configures the main application module with HTTP, serialization, and status pages. */
fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureDatabases()
    configureRouting()
    logger.info("Application module loaded")
}
