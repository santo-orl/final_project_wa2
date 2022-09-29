package it.polito.ticket_catalogue_service.service

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticket_catalogue_service.dtos.*
import it.polito.ticket_catalogue_service.entities.Order
import it.polito.ticket_catalogue_service.entities.OrderType
import it.polito.ticket_catalogue_service.exceptions.NullOrderException
import it.polito.ticket_catalogue_service.exceptions.TicketNotFoundException
import it.polito.ticket_catalogue_service.exceptions.UserNotFoundException
import it.polito.ticket_catalogue_service.repository.OrderRepository
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.repository.TravelcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime

@Service
class OrderService {

    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var ticketRepository: TicketCatRepository
    @Autowired
    lateinit var kafkaTicketPurchasedTemplate: KafkaTemplate<String, CreateTicketsDTO>
    @Autowired
    lateinit var travelcardRepository: TravelcardRepository
    @Autowired
    lateinit var kafkaTravelcardPurchasedTemplate: KafkaTemplate<String, CreateTravelcardDTO>

    @Value("\${topics.ticket-purchased-topic.name}")
    lateinit var ticketPurchasedTopic: String
    @Value("\${topics.travelcard-purchased-topic.name}")
    lateinit var travelcardPurchasedTopic: String


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
        if(orderRepository.findByUserId(userId) == null) throw UserNotFoundException("user not found")
        return orderRepository.findByUserId(userId).map { order -> order.toDTO() }
    }

    suspend fun createOrder(username: String,nTickets: Int, ticketId: Long): Long? {
        if(ticketRepository.findById(ticketId)==null) throw TicketNotFoundException("Ticket not found")
        //val order = orderRepository.save(Order(null, username, "PENDING",nTickets,ticketId,OrderType.TICKET))
        val order = Order(null, username, "PENDING",nTickets,ticketId,OrderType.TICKET)
        println(order.type)
        val o = orderRepository.save(order)
        return o.id
    }

    suspend fun createTravelcardOrder(username: String, travelcardId: Long): Long? {
        if(travelcardRepository.findById(travelcardId)==null) throw TicketNotFoundException("Travelcard not found")
        val order = orderRepository.save(Order(null, username, "PENDING",1,travelcardId,OrderType.TRAVELCARD))
        return order.id
    }

    //TODO inizialmente faceva una richiesta http, ora usa kafka, vedere se funziona
    suspend fun sendPurchasedTicketsToTraveler(nTickets: Int,ticketId:Long,jwt:String, username: String){
        val ticket = ticketRepository.findTicketByTicketId(ticketId)
        if(ticket==null) throw TicketNotFoundException("Ticket id not found")
        val createTicketsDTO = CreateTicketsDTO("create",nTickets,ticket.zid,ticket.validFrom,ticket.ticketType,username)
        kafkaTicketPurchasedTemplate.send(ticketPurchasedTopic, createTicketsDTO)
    }

    suspend fun sendPurchasedTravelcardToTraveler(travelcardId:Long,jwt:String,username: String){
        val travelcard = travelcardRepository.findById(travelcardId)
        val createTravelcardDTO = CreateTravelcardDTO("create",travelcard!!.travelcardType,travelcard!!.zid,username)
        kafkaTravelcardPurchasedTemplate.send(travelcardPurchasedTopic, createTravelcardDTO)
    }

    suspend fun deleteOrder(orderId: Long){
        orderRepository.deleteById(orderId)
    }


}