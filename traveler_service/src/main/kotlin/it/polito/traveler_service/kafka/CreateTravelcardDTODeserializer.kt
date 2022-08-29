package it.polito.traveler_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.CreateTravelcardDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class CreateTravelcardDTODeserializer : Deserializer<CreateTravelcardDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): CreateTravelcardDTO? {
        try {
            return objectMapper.readValue(
                String(
                    data ?: throw SerializationException("Error when deserializing byte[] to CreateTravelcardDTO"),
                    Charsets.UTF_8
                ), CreateTravelcardDTO::class.java
            )
        }catch(e: Exception){
            println("Skipped dirty data in topic")
            throw e
        }
    }

    override fun close() {}

}