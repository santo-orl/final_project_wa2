package it.polito.ticket_catalogue_service.service

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticket_catalogue_service.dtos.PaymentOutcomeDTO
import it.polito.ticket_catalogue_service.entities.OrderType
import it.polito.ticket_catalogue_service.repository.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class KafkaListenerService {

    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var orderService: OrderService

    @KafkaListener(topics = ["PaymentResponseTopic"], groupId = "group-id")
    suspend fun updateOrderByPaymentOutcome(paymentOutcome: String) {
        val paymentOutcome = ObjectMapper().readValue(paymentOutcome, PaymentOutcomeDTO::class.java)
        if (paymentOutcome.outcome) {
            //PaymentService dice che il pagamento è andato a buon fine
            orderRepository.updateOrderStatus(paymentOutcome.orderId, "COMPLETED")
            val order = orderRepository.findById(paymentOutcome.orderId)
            if(order.type==OrderType.TICKET)
                //chiamata a traveler_service per inserire nel db il ticketpurchased
                orderService.sendPurchasedTicketsToTraveler(order.nTickets,order.ticketId,paymentOutcome.jwt,order.userId)
            else
            //chiamata a traveler_service per inserire nel db la travelcardpurchased
                orderService.sendPurchasedTravelcardToTraveler(order.ticketId,paymentOutcome.jwt,order.userId)
        } else {
            orderRepository.updateOrderStatus(paymentOutcome.orderId, "CANCELED")
        }
    }

}

