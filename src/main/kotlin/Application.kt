package com.proschek

import com.proschek.repository.MongoTodoRepository
import com.proschek.repository.TodoRepository
import com.proschek.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val repository = MongoTodoRepository()
    configureHTTP()
    configureSerialization(repository)
    configureDatabases()
//    configureRouting()
}
