package it.polito.payment_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment_service.dtos.PaymentOutcomeDTO
import it.polito.payment_service.dtos.PaymentRequestDTO
import it.polito.payment_service.entities.Transaction
import it.polito.payment_service.repositories.TransactionRepository
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaListenerService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    var paymentResponseTopicName: String = "localhost:29092"

    @KafkaListener(topics = ["PaymentRequestTopic"], groupId = "group-id")
    fun consume(payment: String) {
        lateinit var paymentRequestDTO : PaymentRequestDTO
        try {
            paymentRequestDTO = ObjectMapper().readValue(payment, PaymentRequestDTO::class.java)
            val transaction = Transaction(null, paymentRequestDTO.orderId, paymentRequestDTO.username, "COMPLETED", paymentRequestDTO.totalCost)
            runBlocking {
                transactionRepository.save(transaction)
            }
            //esito positivo dell'operazione
            kafkaTemplate.send(paymentResponseTopicName, PaymentOutcomeDTO(paymentRequestDTO.orderId,true,paymentRequestDTO.jwt))
        }catch(e: Exception){
            println("Skipped dirty data in topic")
        }
    }

}