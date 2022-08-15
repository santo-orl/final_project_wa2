package it.polito.ticket_catalogue_service.controller

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import it.polito.ticket_catalogue_service.dtos.OrderDTO
import it.polito.ticket_catalogue_service.dtos.TicketDTO
import it.polito.ticket_catalogue_service.dtos.UserDetailsDTO
import it.polito.ticket_catalogue_service.dtos.toDTO
import it.polito.ticket_catalogue_service.repository.OrderRepository
import kotlinx.coroutines.flow.map
import it.polito.ticket_catalogue_service.entities.Order
import it.polito.ticket_catalogue_service.exceptions.NullOrderException
import it.polito.ticket_catalogue_service.exceptions.UserNotFoundException
import it.polito.ticket_catalogue_service.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import reactor.core.publisher.Flux
import java.security.Principal


@RestController
class OrderController{

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var orderService: OrderService


    @FlowPreview
    @GetMapping("/orders", produces=[MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getTickets(@RequestHeader("authorization") jwt: String,princ: Principal): ResponseEntity<Flow<OrderDTO>> {
        val res = orderRepository.findByUserId(princ.name).map{ order:Order-> order.toDTO() }
        return ResponseEntity(res,HttpStatus.OK)
    }


    @GetMapping("/orders/{orderId}", produces=[MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getOrders(@RequestHeader("authorization") jwt: String,princ: Principal,@PathVariable orderId: Long): ResponseEntity<OrderDTO?> {
        try {
            var res = orderService.getOrder(orderId)
            return ResponseEntity(res,HttpStatus.OK)
        }catch(e: NullOrderException){ //eccezione se non c'è l'ordine
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/admin/orders", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getAllUsersOrders(
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<Flow<OrderDTO>> {
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(orders)
    }

    @GetMapping("/admin/orders/{userId}", produces = [MediaType.APPLICATION_NDJSON_VALUE]) //returns a json list
    suspend fun getUserOrders(@PathVariable userId: String): ResponseEntity<Flow<OrderDTO>> {
        try {
            return ResponseEntity(orderService.getUserOrders(userId), HttpStatus.OK)
        } catch (e: UserNotFoundException) { //user id not found
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }


}