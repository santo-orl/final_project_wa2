package it.polito.traveler_service.controllers

import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.exceptions.TicketNotFoundException
import it.polito.traveler_service.exceptions.UnauthorizedTicketAccessException
import it.polito.traveler_service.exceptions.UserNotFoundException
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.TransitService
import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import java.util.*
import kotlin.collections.ArrayList

@RestController
class UserDetailsController {

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Autowired
    lateinit var ticketPurchasedService: TicketPurchasedService

    @Autowired
    lateinit var transitService: TransitService

    @GetMapping("/my/profile")
    fun getUserDetailsInfo(@RequestHeader("authorization") jwt: String): ResponseEntity<UserDetailsDTO> {
        //prendo il principal
        val principal = SecurityContextHolder.getContext().authentication.principal;
        //lo casto a UserDetailsImpl altrimenti è Any
        principal as UserDetailsImpl
        //ora posso usarlo per ciò che mi serve
        val username = principal.userr
        var userDetails: UserDetailsDTO
        try {
            userDetails = userDetailsServiceImpl.getUserDetails(username)
        } catch (e: UserNotFoundException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(userDetails, HttpStatus.OK)
    }

    @PostMapping("/my/profile")
    fun insertTravelerInfo(
        @RequestHeader("authorization") jwt: String,
        @RequestBody traveler: UserDetailsDTO
    ): ResponseEntity<String> {
        //lo username va ottenuto dall'utente attualmente loggato
        val principal = SecurityContextHolder.getContext().authentication.principal;
        principal as UserDetailsImpl
        val username = principal.userr
        userDetailsServiceImpl.insertTraveler(username, traveler)
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/my/profile")
    fun updateTravelerInfo(
        @RequestHeader("authorization") jwt: String,
        @RequestBody traveler: UserDetailsDTO
    ): ResponseEntity<String> {
        //lo username va ottenuto dall'utente attualmente loggato
        val principal = SecurityContextHolder.getContext().authentication.principal;
        principal as UserDetailsImpl
        val username = principal.userr
        try {
            userDetailsServiceImpl.updateTraveler(username, traveler)
        } catch (e: UserNotFoundException) {
            return ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(HttpStatus.OK)
    }


    //GET /my/tickets
    @GetMapping("/my/tickets", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllTickets(): ResponseEntity<List<TicketPurchasedDTO>> {
        var tmp: List<TicketPurchasedDTO>
        try {
            val principal = SecurityContextHolder.getContext().authentication.principal;
            principal as UserDetailsImpl
            var idU = principal.id
            tmp = ticketPurchasedService.getAllTickets(idU)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tmp, HttpStatus.OK)

    }

    @GetMapping("/my/tickets/qr/{ticketId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTicketAsQR(@PathVariable ticketId: Long): ResponseEntity<String> {
        val ret: String
        var ticket: TicketPurchasedDTO
        val principal = SecurityContextHolder.getContext().authentication.principal;
        principal as UserDetailsImpl
        //prendo il ticket, al suo interno ha già il jws
        try {
            ticket = ticketPurchasedService.getTicketById(ticketId, principal.userr)
        } catch (e: UnauthorizedTicketAccessException) {
            //se un utente richiede il QR di un biglietto non suo
            return ResponseEntity("This user can't access this ticket",HttpStatus.UNAUTHORIZED)
        }catch(e: TicketNotFoundException){
            return ResponseEntity("Ticket not found",HttpStatus.NOT_FOUND)
        }
        //ritorno il jws codificato in base64
        ret = Base64.getEncoder().encodeToString(ticket.jws.toByteArray())
        return ResponseEntity(ret, HttpStatus.OK)
    }

    //TODO vedere se va sostituita con una comunicazione kafka fra questo microservizio e ticket purchased
    //POST /my/tickets
    @PostMapping("/my/tickets")        //genera i ticket
    fun generateTickets(@RequestBody createTickets: CreateTicketsDTO): ResponseEntity<List<TicketPurchasedDTO>> {
        var ticketsList: ArrayList<TicketPurchasedDTO> = ArrayList()
        try {
            val principal = SecurityContextHolder.getContext().authentication.principal;
            principal as UserDetailsImpl
            var idU = principal.id
            for (i in 0 until createTickets.quantity) {
                var ticket = ticketPurchasedService.createTicket(
                    createTickets.zones,
                    idU,
                    createTickets.validFrom,
                    createTickets.type
                )
                ticketsList.add(ticket)
            }
        } catch (e: Exception) {
            println(e.toString())
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(ticketsList, HttpStatus.CREATED)
    }

    //GET /admin/travelers  only available for ADMIN
    @GetMapping("/admin/travelers", produces = [MediaType.APPLICATION_JSON_VALUE]) //returns a json list
    fun getAllTravelers(): ResponseEntity<List<String>> {
        var travelers: List<String> = userDetailsServiceImpl.getTravelers()
        return ResponseEntity(travelers, HttpStatus.OK)
    }

    //GET /admin/traveler/{userID}/profile only available for ADMIN
    @GetMapping("/admin/traveler/{userID}/profile", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserById(@PathVariable userID: Long): ResponseEntity<UserDetailsDTO> {
        lateinit var usr: UserDetailsDTO
        try {
            usr = userDetailsServiceImpl.getUserById(userID)
        } catch (e: UserNotFoundException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(usr, HttpStatus.OK)
    }

    //GET /admin/traveler/{userID}/tickets  only available for ADMIN
    @GetMapping("/admin/traveler/{userID}/tickets", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserTickets(@PathVariable userID: Long): ResponseEntity<List<TicketPurchasedDTO>> {
        var tickets: List<TicketPurchasedDTO> = ticketPurchasedService.getAllTickets(userID)
        if (tickets.isEmpty()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tickets, HttpStatus.OK)
    }

    @GetMapping("/admin/traveler/transits/{username}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserTransitsInRange(
        @PathVariable username: String,
        @RequestParam("from") from: String,
        @RequestParam("to") to: String
    ): ResponseEntity<List<TransitDTO>> {
        var transits: List<TransitDTO>
        try {
            transits = userDetailsServiceImpl.getUserTransits(username, from, to)
        }catch(e: UserNotFoundException){
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(transits, HttpStatus.OK)
    }

    @GetMapping("/admin/transits", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTransitsInRange(
        @RequestParam("from") from: String,
        @RequestParam("to") to: String
    ): ResponseEntity<List<TransitDTO>> {
        var transits = transitService.getInRange(from, to)
        return ResponseEntity(transits, HttpStatus.OK)
    }

}