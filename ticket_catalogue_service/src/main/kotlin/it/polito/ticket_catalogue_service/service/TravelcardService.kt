package it.polito.ticket_catalogue_service.service

import it.polito.ticket_catalogue_service.dtos.*
import it.polito.ticket_catalogue_service.entities.Ticket
import it.polito.ticket_catalogue_service.entities.Travelcard
import it.polito.ticket_catalogue_service.exceptions.TicketNotFoundException
import it.polito.ticket_catalogue_service.repository.TravelcardRepository
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class TravelcardService {

    @Autowired
    lateinit var kafkaPaymentTemplate: KafkaTemplate<String, Any>

    @Value("\${topics.payment-request-topic.name}")
    lateinit var paymentRequestTopic: String

    @Autowired
    lateinit var travelcardRepository: TravelcardRepository

    suspend fun askForPayment(request: ShopTravelcardRequestDTO, orderId:Long, username:String, jwt:String) {
        val travelcard = travelcardRepository.findById(request.travelcardId)
        //mando le info per il pagamento a PaymentService con Kafka
        val paymentRequest = PaymentRequestDTO(request.cardHolder,request.creditCardNumber.toString(),request.expDate,request.cvv.toString(),orderId, travelcard!!.price,username,jwt)
        runBlocking { //TODO si può togliere runBlocking?
            kafkaPaymentTemplate.send(paymentRequestTopic, paymentRequest)
        }
        //ricevo la risposta sul listener in KafkaListenerService
    }

    suspend fun addNewTravelcard(newTravelcardDTO: TravelcardDTO): TravelcardDTO{
        return travelcardRepository.save(
            Travelcard(
                null,
                newTravelcardDTO.travelcardType,
                newTravelcardDTO.price,
                newTravelcardDTO.minAge,
                newTravelcardDTO.maxAge,
                newTravelcardDTO.zid
            )
        ).toDTO()
    }

    suspend fun removeTravelcard(travelcardId: Long){
        val travelcard = travelcardRepository.findById(travelcardId)
        if(travelcard != null)
            travelcardRepository.delete(travelcard)
    }

    suspend fun updateTravelcard(travelcardId: Long, newTravelcard: TravelcardDTO): TravelcardDTO{
        val travelcard = travelcardRepository.findById(travelcardId)
        if(travelcard==null) throw TicketNotFoundException("Travelcard with id $travelcardId does not exist")
        travelcardRepository.delete(travelcard)
        return travelcardRepository.save(
            Travelcard(
                null,
                newTravelcard.travelcardType,
                newTravelcard.price,
                newTravelcard.minAge,
                newTravelcard.maxAge,
                newTravelcard.zid
            )).toDTO()
    }

}