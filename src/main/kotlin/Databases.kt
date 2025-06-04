 package com.proschek

import com.mongodb.client.*
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.EncoderContext
import org.bson.codecs.DecoderContext
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.proschek.model.ID
import com.proschek.mongodb.TodoDocument
import org.bson.BsonType

 lateinit var database: MongoDatabase

 fun Application.configureDatabases() {
     val config = ConfigFactory.load("application.conf")
     val uri = config.getConfig("ktor").getConfig("mongodb").getString("connectionString")
     val databaseUri = config.getConfig("ktor").getConfig("mongodb").getString("databaseName")

     // Create a more robust ID codec that can handle different BSON types
     val idCodec = object : Codec<ID> {
         override fun encode(writer: BsonWriter, value: ID, encoderContext: EncoderContext) {
             writer.writeString(value.toString())
         }

         override fun decode(reader: BsonReader, decoderContext: DecoderContext): ID {
             // Check the current BSON type and handle accordingly
             return when (reader.currentBsonType) {
                 BsonType.STRING -> {
                     ID.fromString(reader.readString())
                 }
                 BsonType.OBJECT_ID -> {
                     // Convert ObjectId to string and then to ID
                     val objectId = reader.readObjectId()
                     ID.fromString(objectId.toString())
                 }
                 else -> {
                     // Skip the value and return a default ID
                     reader.skipValue()
                     ID()
                 }
             }
         }

         override fun getEncoderClass(): Class<ID> = ID::class.java
     }

     // Rest of your configuration code...
     val customCodecs = CodecRegistries.fromCodecs(idCodec)

     // Create POJO codec provider
     val pojoCodecProvider = PojoCodecProvider.builder()
         .automatic(true)
         .register(TodoDocument::class.java)
         .build()

     val pojoCodecRegistry = CodecRegistries.fromProviders(pojoCodecProvider)

     // Combine registries
     val codecRegistry = CodecRegistries.fromRegistries(
         customCodecs,
         pojoCodecRegistry,
         MongoClientSettings.getDefaultCodecRegistry()
     )

     // Create client with codec registry
     val settings = MongoClientSettings.builder()
         .applyConnectionString(ConnectionString(uri))
         .codecRegistry(codecRegistry)
         .build()

     val mongoClient = MongoClients.create(settings)
     database = mongoClient.getDatabase(databaseUri)

     log.info("MongoDB database initialized successfully")

     monitor.subscribe(ApplicationStopped) {
         mongoClient.close()
         log.info("MongoDB connection closed")
     }
 }

