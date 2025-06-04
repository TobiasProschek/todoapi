package com.proschek.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object IDSerializer : KSerializer<ID> {
    private val uuidSerializer = UUIDSerializer()

    override val descriptor = PrimitiveSerialDescriptor("ID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ID) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): ID {
        val uuidString = decoder.decodeString()
        return ID(UUID.fromString(uuidString))
    }
}

// Then update your ID class to use this serializer:
@Serializable(with = IDSerializer::class)
data class ID(val value: UUID = UUID.randomUUID()) {
    override fun toString(): String = value.toString()
    companion object {
        fun fromString(id: String): ID = ID(UUID.fromString(id))
    }
}

class IDDeserializer : StdDeserializer<ID>(ID::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ID {
        val uuidString = p.valueAsString
        return ID(UUID.fromString(uuidString))
    }
}
