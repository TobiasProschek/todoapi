 package com.proschek

import com.mongodb.client.*
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.proschek.model.Todo
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import org.bson.Document

 lateinit var database: MongoDatabase

 // Make collection lazy so it's only initialized when first accessed
 val collection: MongoCollection<Todo> by lazy {
     database.getCollection("todos")
 }

 fun Application.configureDatabases() {
     val config = ConfigFactory.load("application.conf")
     val uri = config.getConfig("ktor").getConfig("mongodb").getString("connectionString")
     val databaseUri = config.getConfig("ktor").getConfig("mongodb").getString("databaseName")

     val mongoClient = MongoClient.create(uri) // Make sure you're using the right MongoClient

     database = mongoClient.getDatabase(databaseUri)

     log.info("MongoDB database initialized successfully")

     monitor.subscribe(ApplicationStopped) {
         mongoClient.close()
         log.info("MongoDB connection closed")
     }
 }