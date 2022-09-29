package it.polito.payment_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment_service.dtos.PaymentOutcomeDTO
import it.polito.payment_service.dtos.PaymentRequestDTO
import it.polito.payment_service.entities.Transaction
import it.polito.payment_service.repositories.TransactionRepository
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaListenerService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, PaymentOutcomeDTO>

    @Value("\$topics.payment-response-topic.name")
    lateinit var paymentResponseTopicName: String

    @KafkaListener(topics = ["PaymentRequestTopic"], containerFactory = "kafkaListenerContainerFactory")
    fun consume(payment: PaymentRequestDTO) {
        try {
            val transaction = Transaction(null, payment.orderId, payment.username, "COMPLETED", payment.totalCost)
            runBlocking {
                transactionRepository.save(transaction)
            }
            //esito positivo dell'operazione
            kafkaTemplate.send("PaymentRespTopic", PaymentOutcomeDTO(payment.orderId, true,false,payment.jwt))
        }catch(e: DataAccessException) {
            //errore nel db, mandare a ticket_catalogue esito negativo dell'operazione
            println("DB error")
            kafkaTemplate.send("PaymentRespTopic", PaymentOutcomeDTO(payment.orderId, true,true,payment.jwt))
        }catch(e: Exception){
            //errore in kafka
            println("Skipped dirty data in topic")
            kafkaTemplate.send("PaymentRespTopic", PaymentOutcomeDTO(payment.orderId, true,true,payment.jwt))
        }
    }

}