package it.polito.traveler_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.traveler_service.dtos.CreateTicketsDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<CreateTicketsDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): CreateTicketsDTO? {
        try {
            return objectMapper.readValue(
                String(
                    data ?: throw SerializationException("Error when deserializing byte[] to CreateTicketsDTO"), UTF_8
                ), CreateTicketsDTO::class.java
            )
        }catch(e: Exception){
            println("Skipped dirty data in topic")
            throw e
        }
    }

    override fun close() {}

}