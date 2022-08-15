package it.polito.payment_service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment_service.dtos.PaymentRequestDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<PaymentRequestDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentRequestDTO? {
        try {
            return objectMapper.readValue(
                String(
                    data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
                ), PaymentRequestDTO::class.java
            )
        }catch(e: Exception){
            println("Skipped dirty data in topic")
            throw e
        }
    }

    override fun close() {}

}