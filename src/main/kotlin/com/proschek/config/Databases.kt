package com.proschek.config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.log
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.bson.codecs.kotlinx.ObjectIdSerializer

lateinit var database: MongoDatabase

/** Configures MongoDB database connection using application configuration. */
@OptIn(ExperimentalSerializationApi::class)
fun Application.configureDatabases() {
    val isCI = System.getenv("CI") == "true" || System.getenv("GITHUB_ACTIONS") == "true"
    val config = if (isCI) ConfigFactory.load("application-ci.conf") else ConfigFactory.load("application.conf")
    val uri = config.getConfig("ktor").getConfig("mongodb").getString("connectionString")
    val databaseUri = config.getConfig("ktor").getConfig("mongodb").getString("databaseName")

    val mongoClient = MongoClient.create(uri) // Make sure you're using the right MongoClient

    database = mongoClient.getDatabase(databaseUri)

    SerializersModule {
        contextual(ObjectIdSerializer)
    }

    log.info("MongoDB database initialized successfully")

    monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
        log.info("MongoDB connection closed")
    }
}
