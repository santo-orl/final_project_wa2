package it.polito.ticket_catalogue_service.controller

import it.polito.ticket_catalogue_service.dtos.*
import it.polito.ticket_catalogue_service.entities.Order
import it.polito.ticket_catalogue_service.entities.Ticket
import it.polito.ticket_catalogue_service.entities.Travelcard
import it.polito.ticket_catalogue_service.exceptions.TicketNotFoundException
import it.polito.ticket_catalogue_service.repository.OrderRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.service.OrderService
import it.polito.ticket_catalogue_service.service.TicketCatService
import it.polito.ticket_catalogue_service.service.TravelcardService
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
    lateinit var travelcardService: TravelcardService
    @Autowired
    lateinit var orderService: OrderService

    @FlowPreview
    @GetMapping("/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getTickets(
        @RequestHeader("authorization") jwt: String,
        princ: Principal
    ): ResponseEntity<Flow<Ticket>> {
        return ResponseEntity(ticketCatService.getTickets(), HttpStatus.OK)
    }

    @FlowPreview
    @GetMapping("/travelcards", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getTravelcards(
        @RequestHeader("authorization") jwt: String,
        princ: Principal
    ): ResponseEntity<Flow<Travelcard>> {
        return ResponseEntity(ticketCatService.getTravelcards(), HttpStatus.OK)
    }

    @PostMapping("/shop/tickets")
    suspend fun shopTickets(
        @RequestHeader("Authorization") jwt: String,
        @RequestBody req: ShopRequestDTO,
        princ: Principal
    ): ResponseEntity<Long?> {
        //controllo sugli autenticati
        //vedo se hanno restrizioni e se l'utente rientra in esse
        if (ticketCatService.isValid(jwt, req.ticketId)) {
            try {
                //salvo l'ordine nel db con status pending
                val orderId = orderService.createOrder(princ.name, req.nTickets, req.ticketId)
                //procedo a chiedere il pagamento a PaymentService
                ticketCatService.askForPayment(req, orderId!!, princ.name, jwt)
                return ResponseEntity(orderId, HttpStatus.OK)
            } catch (e: TicketNotFoundException) {
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }
        } else { //se l'utente non rientra nelle restrizioni
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/shop/travelcard")
    suspend fun shopTravelcard(
        @RequestHeader("Authorization") jwt: String,
        @RequestBody req: ShopTravelcardRequestDTO,
        princ: Principal
    ): ResponseEntity<Long?> {
        try {
            //salvo l'ordine nel db con status pending
            val orderId = orderService.createTravelcardOrder(princ.name, req.travelcardId)
            //procedo a chiedere il pagamento a PaymentService
            travelcardService.askForPayment(req, orderId!!, princ.name, jwt)
            return ResponseEntity(orderId, HttpStatus.OK)
        } catch (e: TicketNotFoundException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/admin/tickets", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun addTicket(
        @RequestBody addTicketRequest: TicketDTO,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<TicketDTO> {
        return ResponseEntity(ticketCatService.addNewTicket(addTicketRequest), HttpStatus.CREATED)
    }

    @PostMapping("/admin/travelcards", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun addTravelcard(
        @RequestBody addTravelcardRequest: TravelcardDTO,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<TravelcardDTO> {
        return ResponseEntity(travelcardService.addNewTravelcard(addTravelcardRequest), HttpStatus.CREATED)
    }

    @DeleteMapping("/admin/tickets/{ticketId}")
    suspend fun removeTicket(
        @PathVariable("ticketId") ticketId: Long,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<Long> {
        ticketCatService.removeTicket(ticketId)
        return ResponseEntity(ticketId, HttpStatus.OK)
    }

    @DeleteMapping("/admin/travelcards/{travelcardId}")
    suspend fun removeTravelcard(
        @PathVariable("travelcardId") travelcardId: Long,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<Long> {
        travelcardService.removeTravelcard(travelcardId)
        return ResponseEntity(travelcardId, HttpStatus.OK)
    }

    @PutMapping("/admin/tickets/{ticketId}", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun updateTicket(
        @PathVariable("ticketId") ticketId: Long,
        @RequestBody newTicket: TicketDTO,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<TicketDTO> {
        return try {
            ResponseEntity(ticketCatService.updateTicket(ticketId, newTicket), HttpStatus.CREATED)
        } catch (e: TicketNotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/admin/travelcards/{travelcardId}", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun updateTravelcard(
        @PathVariable("travelcardId") travelcardId: Long,
        @RequestBody newTravelcard: TravelcardDTO,
        @RequestHeader("authorization") jwt: String
    ): ResponseEntity<TravelcardDTO> {
        return try {
            ResponseEntity(travelcardService.updateTravelcard(travelcardId, newTravelcard), HttpStatus.CREATED)
        } catch (e: TicketNotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

}