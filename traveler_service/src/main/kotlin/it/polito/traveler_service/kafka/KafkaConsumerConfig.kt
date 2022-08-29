package it.polito.traveler_service.kafka

import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.CreateTravelcardDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties


@EnableKafka
@Configuration
class KafkaConsumerConfig(

) {
    private val servers: String = "localhost:29092"

    @Value(value = "\${ticket.topic.group.id}")
    private val ticketGroupId: String? = null
    @Value(value = "\${travelcard.topic.group.id}")
    private val travelcardGroupId: String? = null


    // configurazione per ricevere CreateTicketsDTO

    @Bean
    fun ticketConsumerFactory(): ConsumerFactory<String?, CreateTicketsDTO?> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        props[ConsumerConfig.GROUP_ID_CONFIG] = ticketGroupId!!
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = CreateTicketsDTODeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun ticketKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CreateTicketsDTO>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CreateTicketsDTO>()
        factory.consumerFactory = ticketConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true;
        return factory
    }


    //configurazione per ricevere CreateTravelcardDTO

    @Bean
    fun travelcardConsumerFactory(): ConsumerFactory<String?, CreateTravelcardDTO?> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        props[ConsumerConfig.GROUP_ID_CONFIG] = travelcardGroupId!!
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = CreateTravelcardDTODeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun travelcardKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CreateTravelcardDTO>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CreateTravelcardDTO>()
        factory.consumerFactory = travelcardConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true;
        return factory
    }



}