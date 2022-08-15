package it.polito.ticket_catalogue_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.lab5.lab5_ticketcatalogueservice.dtos.PaymentOutcomeDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<PaymentOutcomeDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentOutcomeDTO? {
        try {
            return objectMapper.readValue(
                String(
                    data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
                ), PaymentOutcomeDTO::class.java
            )
        }catch(e: Exception){
            println("Skipped dirty data in topic")
            throw e
        }
    }

    override fun close() {}

}