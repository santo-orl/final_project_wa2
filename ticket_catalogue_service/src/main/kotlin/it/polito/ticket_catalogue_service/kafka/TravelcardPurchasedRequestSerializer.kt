package it.polito.ticket_catalogue_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticket_catalogue_service.dtos.CreateTicketsDTO
import it.polito.ticket_catalogue_service.dtos.CreateTravelcardDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory

class TravelcardPurchasedRequestSerializer : Serializer<CreateTravelcardDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: CreateTravelcardDTO?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing CreateTravelcardDTO to ByteArray[]")
        )
    }

    override fun close() {}
}
