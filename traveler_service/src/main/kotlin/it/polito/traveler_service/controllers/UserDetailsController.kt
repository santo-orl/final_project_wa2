package it.polito.traveler_service.controllers

import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class UserDetailsController {

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Autowired
    lateinit var ticketPurchasedService: TicketPurchasedService

    @GetMapping("/my/profile")
    fun getUserDetailsInfo(@RequestHeader("authorization") jwt: String): ResponseEntity<UserDetailsDTO> {
        //prendo il principal
        val principal = SecurityContextHolder.getContext().authentication.principal;
        //lo casto a UserDetailsImpl altrimenti è Any
        principal as UserDetailsImpl
        //ora posso usarlo per ciò che mi serve
        val username = principal.userr
        val userDetails: UserDetailsDTO? = userDetailsServiceImpl.getUserDetails(username)
        return ResponseEntity(userDetails, HttpStatus.OK)
    }

    @PutMapping("/my/profile")
    fun updateTravelerInfo(@RequestHeader("authorization") jwt: String, @RequestBody traveler: UserDetailsDTO): ResponseEntity<String> {
        //lo username va ottenuto dall'utente attualmente loggato
        //finché non finiamo la parte di security possiamo usarne uno di prova
        val principal = SecurityContextHolder.getContext().authentication.principal;
        principal as UserDetailsImpl
        val username = principal.userr

        userDetailsServiceImpl.updateTraveler(username,traveler)
        return ResponseEntity(HttpStatus.OK)
    }


    //GET /my/tickets
    @GetMapping("/my/tickets", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllTickets(): ResponseEntity<List<TicketPurchasedDTO>> {

        var tmp:List<TicketPurchasedDTO>

        try {
            val principal = SecurityContextHolder.getContext().authentication.principal;
            principal as UserDetailsImpl
            var idU = principal.id
            tmp = ticketPurchasedService.getAllTickets(idU)

        } catch (e: Exception) {

            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(tmp,HttpStatus.OK)

    }

    //POST /my/tickets
    @PostMapping("/my/tickets")        //genera i ticket
    fun generateTickets(@RequestBody createTickets: CreateTicketsDTO): ResponseEntity<List<TicketPurchasedDTO>> {
        var ticketsList: ArrayList<TicketPurchasedDTO> = ArrayList()
        try {
            val principal = SecurityContextHolder.getContext().authentication.principal;
            principal as UserDetailsImpl
            var idU = principal.id
            for(i in 0 until createTickets.quantity){
                var ticket = ticketPurchasedService.createTicket(createTickets.zones, idU, createTickets.validFrom,createTickets.type)
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
        lateinit var usr:UserDetailsDTO
        try {
            usr = userDetailsServiceImpl.getUserById(userID)
        } catch (e: Exception) {

            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        return ResponseEntity(usr, HttpStatus.OK)

    }

    //GET /admin/traveler/{userID}/tickets  only available for ADMIN
        @GetMapping("/admin/traveler/{userID}/tickets", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserTickets(@PathVariable userID: Long): ResponseEntity<List<TicketPurchasedDTO>> {

        var tickets: List<TicketPurchasedDTO> = ticketPurchasedService.getAllTickets(userID)

        if(tickets.isEmpty()){
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        return ResponseEntity(tickets, HttpStatus.OK)

    }

}