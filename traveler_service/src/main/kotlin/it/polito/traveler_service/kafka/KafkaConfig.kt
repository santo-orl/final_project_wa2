package it.polito.traveler_service.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${topics.ticket-purchased-topic.name}")
    private val ticketTopic: String,
    @Value("\${topics.travelcard-purchased-topic.name}")
    private val travelcardTopic: String
) {
    private val servers: String = "localhost:29092"
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    @Bean
    fun createTicketPurchasedTopic(): NewTopic {
        return NewTopic(
            ticketTopic,
            1,
            1
        )
    }

    @Bean
    fun createTravlecardPurchasedTopic(): NewTopic {
        return NewTopic(
            travelcardTopic,
            1,
            1
        )
    }

}