package it.polito.ticket_catalogue_service.service

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticket_catalogue_service.dtos.*
import it.polito.ticket_catalogue_service.entities.Order
import it.polito.ticket_catalogue_service.exceptions.NullOrderException
import it.polito.ticket_catalogue_service.exceptions.UserNotFoundException
import it.polito.ticket_catalogue_service.repository.OrderRepository
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class OrderService {

    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var ticketRepository: TicketCatRepository
    @Value("\${travelerServiceUrl}")
    lateinit var travelerServiceUrl: String
    var client: WebClient = WebClient.create()


    suspend fun getOrder(orderId: Long): OrderDTO {
        var tmp = orderRepository.findById(orderId)
        if (tmp == null) {
            throw NullOrderException("bad request")
        } else {
            return tmp.toDTO()
        }
    }

    fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAll()
            .map { order -> order.toDTO() }
    }

    suspend fun getUserOrders(userId: String): Flow<OrderDTO> {
        var ret = orderRepository.findByUserId(userId).map { order -> order.toDTO() }
        if (ret == null) throw UserNotFoundException("user not found")
        return ret
    }


    @KafkaListener(topics = ["PaymentResponseTopic"], groupId = "group-id")
    suspend fun updateOrderByPaymentOutcome(paymentOutcome: String) {
        val paymentOutcome = ObjectMapper().readValue(paymentOutcome, PaymentOutcomeDTO::class.java)
        if (paymentOutcome.outcome) {
            //PaymentService dice che il pagamento è andato a buon fine
            orderRepository.updateOrderStatus(paymentOutcome.orderId, "COMPLETED")
            val order = orderRepository.findById(paymentOutcome.orderId)
            //Chiamata a TravelerService per inserire nel db il ticketpurchased
            sendPurchasedTicketsToTraveler(order.nTickets,order.ticketId,paymentOutcome.jwt)
        } else {
            orderRepository.updateOrderStatus(paymentOutcome.orderId, "CANCELED")
        }
    }


    suspend fun createOrder(username: String,nTickets: Int, ticketId: Long): Long? {
        val order = orderRepository.save(Order(null, username, "PENDING",nTickets,ticketId))
        return order.id
    }

    suspend fun sendPurchasedTicketsToTraveler(nTickets: Int,ticketId:Long,jwt:String){
        val ticket = ticketRepository.findById(ticketId)
        var ret = client.post()
            .uri(travelerServiceUrl + "/my/tickets")
            .header("Authorization", jwt)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody<List<TicketDTO>>()
    }

}