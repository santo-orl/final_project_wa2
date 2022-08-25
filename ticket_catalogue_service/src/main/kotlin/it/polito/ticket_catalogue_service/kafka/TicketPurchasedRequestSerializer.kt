package it.polito.ticket_catalogue_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticket_catalogue_service.dtos.CreateTicketsDTO
import it.polito.ticket_catalogue_service.dtos.PaymentRequestDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class TicketPurchasedRequestSerializer : Serializer<CreateTicketsDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: CreateTicketsDTO?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing Product to ByteArray[]")
        )
    }

    override fun close() {}
}
