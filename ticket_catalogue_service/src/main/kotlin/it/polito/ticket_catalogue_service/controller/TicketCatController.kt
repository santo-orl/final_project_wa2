package it.polito.ticket_catalogue_service.controller

import it.polito.ticket_catalogue_service.dtos.ShopRequestDTO
import it.polito.ticket_catalogue_service.dtos.TicketDTO
import it.polito.ticket_catalogue_service.dtos.UserDetailsDTO
import it.polito.ticket_catalogue_service.entities.Order
import it.polito.ticket_catalogue_service.entities.Ticket
import it.polito.ticket_catalogue_service.repository.OrderRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.service.OrderService
import it.polito.ticket_catalogue_service.service.TicketCatService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.bind.annotation.*
import reactor.netty.http.server.HttpServer
import java.security.Principal

@RestController
class TicketCatController {

    @Autowired
    lateinit var ticketCatService: TicketCatService
    @Autowired
    lateinit var orderService: OrderService

    @FlowPreview
    @GetMapping("/tickets", produces=[MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getTickets(@RequestHeader("authorization") jwt: String,princ:Principal): ResponseEntity<Flow<Ticket>> {
        return ResponseEntity(ticketCatService.getTickets(), HttpStatus.OK)
    }

    @PostMapping("/shop")
     suspend fun shopTickets(@RequestHeader("Authorization") jwt: String, @RequestBody req : ShopRequestDTO,princ: Principal): ResponseEntity<Long?> {
         //controllo sugli autenticati
         //vedo se hanno restrizioni e se l'utente rientra in esse

         if(ticketCatService.isValid(jwt,req.ticketId)){
             //salvo l'ordine nel db con status pending
             val orderId=orderService.createOrder(princ.name,req.nTickets,req.ticketId)
             //procedo a chiedere il pagamento a PaymentService
             ticketCatService.askForPayment(req,orderId!!,princ.name,jwt)
             return ResponseEntity(orderId,HttpStatus.OK)
         }
         else{ //se l'utente non rientra nelle restrizioni
             return ResponseEntity(HttpStatus.BAD_REQUEST)
         }

     }

    @PostMapping("/admin/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun addTicket(@RequestBody addTicketRequest: TicketDTO, @RequestHeader("authorization") jwt: String ): ResponseEntity<TicketDTO>{
        return ResponseEntity(ticketCatService.addNewTicket(addTicketRequest),HttpStatus.CREATED)
    }

    @DeleteMapping("/admin/tickets/{ticketId}")
    suspend fun removeTicket(@PathVariable ticketId: Long, @RequestHeader("authorization") jwt: String ): ResponseEntity<Long>{
        ticketCatService.removeTicket(ticketId)
        return ResponseEntity(ticketId,HttpStatus.OK)
    }

    @PutMapping("/admin/tickets/{ticketId}", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun updateTicket(@PathVariable ticketId: Long, @RequestBody newTicket: TicketDTO, @RequestHeader("authorization") jwt: String ): ResponseEntity<TicketDTO>{
        return ResponseEntity(ticketCatService.updateTicket(ticketId,newTicket),HttpStatus.CREATED)
    }

}