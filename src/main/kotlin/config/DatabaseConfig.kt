package com.proschek.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import io.ktor.server.application.*

object DatabaseConfig {
    private lateinit var client: MongoClient
    lateinit var database: MongoDatabase
        private set

    fun init(application: Application) {
        val environment = application.environment.config.property("ktor.environment").getString()
        val connectionString = application.environment.config.property("database.connectionString").getString()
        val databaseName = application.environment.config.property("database.name").getString()

        application.log.info("Connecting to MongoDB with environment: $environment")

        client = MongoClients.create(connectionString)
        database = client.getDatabase(databaseName)

        application.log.info("Connected to MongoDB database: $databaseName")
    }

    fun close() {
        client.close()
    }
}