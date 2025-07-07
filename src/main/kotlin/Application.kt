package com.proschek

import com.proschek.config.configureDatabases
import com.proschek.plugins.configureHTTP
import com.proschek.plugins.configureRouting
import com.proschek.plugins.configureSerialization
import com.proschek.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    EngineMain.main(args)
}

private val logger = LoggerFactory.getLogger("Application")

fun Application.module() {
    logger.info("Application module loaded")
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureDatabases()
    configureRouting()
}
