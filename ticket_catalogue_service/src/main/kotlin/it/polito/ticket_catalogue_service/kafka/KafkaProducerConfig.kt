package it.polito.ticket_catalogue_service.kafka

import it.polito.ticket_catalogue_service.dtos.CreateTicketsDTO
import it.polito.ticket_catalogue_service.dtos.CreateTravelcardDTO
import it.polito.ticket_catalogue_service.dtos.PaymentRequestDTO
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory


@Configuration
class KafkaProducerConfig(
) {
    private val servers: String = "localhost:29092"

    //per il payment
    @Bean
    fun paymentRequestProducerFactory(): ProducerFactory<String, PaymentRequestDTO> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = PaymentRequestSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaPaymentTemplate(): KafkaTemplate<String, PaymentRequestDTO> {
        return KafkaTemplate(paymentRequestProducerFactory())
    }

    //per il ticketPurchased
    @Bean
    fun ticketPurchasedProducerFactory(): ProducerFactory<String, CreateTicketsDTO> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TicketPurchasedRequestSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTicketPurchasedTemplate(): KafkaTemplate<String, CreateTicketsDTO> {
        return KafkaTemplate(ticketPurchasedProducerFactory())
    }

    //per la travelcard purchased

    @Bean
    fun travelcardPurchasedProducerFactory(): ProducerFactory<String, CreateTravelcardDTO> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TravelcardPurchasedRequestSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTravelcardPurchasedTemplate(): KafkaTemplate<String, CreateTravelcardDTO> {
        return KafkaTemplate(travelcardPurchasedProducerFactory())
    }

}
