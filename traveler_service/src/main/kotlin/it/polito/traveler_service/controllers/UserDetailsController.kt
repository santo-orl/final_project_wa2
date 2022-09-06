package it.polito.traveler_service.controllers

import it.polito.traveler_service.dtos.*
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.exceptions.TicketNotFoundException
import it.polito.traveler_service.exceptions.UnauthorizedTicketAccessException
import it.polito.traveler_service.exceptions.UserNotFoundException
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.TransitService
import it.polito.traveler_service.services.TravelcardPurchasedService
import it.polito.traveler_service.services.UserDetailsServiceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
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
    @Autowired
    lateinit var travelcardPurchasedService: TravelcardPurchasedService

    @GetMapping("/my/profile")
    suspend fun getUserDetailsInfo(@RequestHeader("authorization") jwt: String): ResponseEntity<UserDetailsDTO> {
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
    suspend fun insertTravelerInfo(
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
    suspend fun updateTravelerInfo(
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
    suspend fun getAllTickets(): ResponseEntity<Flow<TicketPurchasedDTO>> {
        var tmp: Flow<TicketPurchasedDTO>
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

    @GetMapping("/my/travelcards", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllTravelcards(): ResponseEntity<Flow<TravelcardPurchasedDTO>> {
        var tmp: Flow<TravelcardPurchasedDTO>
        try {
            val principal = SecurityContextHolder.getContext().authentication.principal;
            principal as UserDetailsImpl
            var idU = principal.id
            tmp = travelcardPurchasedService.getAllTravelcards(idU)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tmp, HttpStatus.OK)

    }

    @GetMapping("/my/tickets/qr/{ticketId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getTicketAsQR(@PathVariable ticketId: Long): ResponseEntity<String> {
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

    @GetMapping("/my/travelcards/qr/{travelcardId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getTravelcardsAsQR(@PathVariable travelcardId: Long): ResponseEntity<String> {
        val ret: String
        var travelcard: TravelcardPurchasedDTO
        val principal = SecurityContextHolder.getContext().authentication.principal;
        principal as UserDetailsImpl
        try {
            //prendo la travelcard, al suo interno ha già il jws
            travelcard = travelcardPurchasedService.getTravelcardById(travelcardId, principal.userr)
            //controllo che la travelcard sia valida
            if(travelcardPurchasedService.isExpired(travelcard.sub))
                return ResponseEntity("This travelcard is not valid", HttpStatus.UNAUTHORIZED)
        } catch (e: UnauthorizedTicketAccessException) {
            //se un utente richiede il QR di una travelcard non sua
            return ResponseEntity("This user can't access this travelcard",HttpStatus.UNAUTHORIZED)
        }catch(e: TicketNotFoundException){
            return ResponseEntity("Travelcard not found",HttpStatus.NOT_FOUND)
        }
        //ritorno il jws codificato in base64
        ret = Base64.getEncoder().encodeToString(travelcard.jws.toByteArray())
        return ResponseEntity(ret, HttpStatus.OK)
    }

    //TODO questa non si usa più perché è stata sostituita dalla comunicazione con kafka
    //TODO se createTicketPurchased in TicketPurchasedService funziona, eliminare questa funzione
    @PostMapping("/my/tickets")
    suspend        //genera i ticket
    fun generateTickets(@RequestBody createTickets: CreateTicketsDTO): ResponseEntity<Flow<TicketPurchasedDTO>> {
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
        return ResponseEntity(ticketsList.asFlow(), HttpStatus.CREATED)
    }

    //GET /admin/travelers  only available for ADMIN
    @GetMapping("/admin/travelers", produces = [MediaType.APPLICATION_JSON_VALUE]) //returns a json list
    fun getAllTravelers(): ResponseEntity<Flow<String>> {
        var travelers: Flow<String> = userDetailsServiceImpl.getTravelers()
        return ResponseEntity(travelers, HttpStatus.OK)
    }

    //GET /admin/traveler/{userID}/profile only available for ADMIN
    @GetMapping("/admin/traveler/{userID}/profile", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getUserById(@PathVariable userID: Long): ResponseEntity<UserDetailsDTO> {
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
    suspend fun getUserTickets(@PathVariable userID: Long): ResponseEntity<Flow<TicketPurchasedDTO>> {
        var tickets: Flow<TicketPurchasedDTO> = ticketPurchasedService.getAllTickets(userID)
        if (tickets.toList().isEmpty()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tickets, HttpStatus.OK)
    }

    @GetMapping("/admin/traveler/{userID}/travelcards", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getUserTravelcards(@PathVariable userID: Long): ResponseEntity<Flow<TravelcardPurchasedDTO>> {
        var travelcards: Flow<TravelcardPurchasedDTO> = travelcardPurchasedService.getAllTravelcards(userID)
        if (travelcards.toList().isEmpty()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(travelcards, HttpStatus.OK)
    }

    @GetMapping("/admin/traveler/transits/{username}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getUserTransitsInRange(
        @PathVariable username: String,
        @RequestParam("from") from: String,
        @RequestParam("to") to: String
    ): ResponseEntity<Flow<TransitDTO>> {
        var transits: Flow<TransitDTO>
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
    ): ResponseEntity<Flow<TransitDTO>> {
        var transits = transitService.getInRange(from, to)
        return ResponseEntity(transits, HttpStatus.OK)
    }

}