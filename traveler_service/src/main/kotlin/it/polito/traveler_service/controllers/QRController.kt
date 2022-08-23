package it.polito.traveler_service.controllers

import it.polito.traveler_service.dtos.TicketValidatedDTO
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.TransitService
import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class QRController {

    @Value("\${jwtValidationKey}")
    lateinit var jwtValidationKey: String

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Autowired
    lateinit var ticketPurchasedService: TicketPurchasedService

    @Autowired
    lateinit var transitService: TransitService

    @GetMapping("/qr/validation")
    fun getValidationKey(@RequestHeader("authorization") jwt: String): ResponseEntity<String> {
        return ResponseEntity(jwtValidationKey, HttpStatus.OK)
    }

    @PostMapping("/qr/ticket-validated")
    fun ticketValidated(@RequestHeader("authorization") jwt: String, @RequestBody ticketValidated: TicketValidatedDTO): ResponseEntity<Any> {
        //rimuovo il ticket purchased dall'elenco
        ticketPurchasedService.removeTicket(ticketValidated.ticketId)
        //aggiorno i transit dell'utente aggiungendo la data
        userDetailsServiceImpl.addTransit(ticketValidated.username,ticketValidated.date)
        return ResponseEntity(HttpStatus.OK)
    }

}