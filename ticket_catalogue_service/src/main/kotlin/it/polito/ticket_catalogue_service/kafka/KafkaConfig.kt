package it.polito.ticket_catalogue_service.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${topics.payment-request-topic.name}")
    private val paymentRequestTopic: String,
    @Value("\${topics.ticket-purchased-topic.name}")
    private val ticketPurchasedTopic: String,
    @Value("\${topics.travelcard-purchased-topic.name}")
    private val travelcardPurchasedTopic: String
) {
    private val servers: String = "localhost:29092"
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    @Bean
    fun createPaymentRequestTopic(): NewTopic {
        return NewTopic(
            paymentRequestTopic,
            1,
            1
        )
    }

    @Bean
    fun createPaymentResponseTopic(): NewTopic {
        return NewTopic(
            "PaymentResponseTopic",
            1,
            1
        )
    }

    @Bean
    fun createTicketPurchasedTopic(): NewTopic {
        return NewTopic(
            ticketPurchasedTopic,
            1,
            1
        )
    }

    @Bean
    fun createTravelcardPurchasedTopic(): NewTopic {
        return NewTopic(
            travelcardPurchasedTopic,
            1,
            1
        )
    }

}