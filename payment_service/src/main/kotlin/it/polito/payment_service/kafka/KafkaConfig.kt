package it.polito.payment_service.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${topics.payment-request-topic.name}")
    private val topicRequest: String,
    @Value("\${topics.payment-response-topic.name}")
    private val topicResponse: String
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
            topicRequest,
            1,
            1
        )
    }

    @Bean
    fun createPaymentResponseTopic(): NewTopic {
        return NewTopic(
            topicResponse,
            1,
            1
        )
    }
}