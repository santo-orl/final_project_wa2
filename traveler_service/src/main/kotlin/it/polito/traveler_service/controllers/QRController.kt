package it.polito.traveler_service.controllers

import it.polito.traveler_service.dtos.TicketValidatedDTO
import it.polito.traveler_service.dtos.TravelcardValidatedDTO
import it.polito.traveler_service.exceptions.TicketNotFoundException
import it.polito.traveler_service.exceptions.UserNotFoundException
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.TransitService
import it.polito.traveler_service.services.TravelcardPurchasedService
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

    @Autowired
    lateinit var travelcardPurchasedService: TravelcardPurchasedService

    @GetMapping("/qr/validation")
    fun getValidationKey(@RequestHeader("authorization") jwt: String): ResponseEntity<String> {
        return ResponseEntity(jwtValidationKey, HttpStatus.OK)
    }

    @PostMapping("/qr/ticket-validated")
    fun ticketValidated(
        @RequestHeader("authorization") jwt: String,
        @RequestBody ticketValidated: TicketValidatedDTO
    ): ResponseEntity<String> {
        //rimuovo il ticket purchased dall'elenco
        try {
            ticketPurchasedService.removeTicket(ticketValidated.ticketId)
        } catch (e: TicketNotFoundException) {
            return ResponseEntity("Ticket not found", HttpStatus.NOT_FOUND)
        }
        //aggiorno i transit dell'utente aggiungendo la data
        try {
            userDetailsServiceImpl.addTransit(ticketValidated.username, ticketValidated.date)
        } catch (e: UserNotFoundException) {
            return ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/qr/travelcard-validated")
    fun travelcardValidated(
        @RequestHeader("authorization") jwt: String,
        @RequestBody travelcardValidated: TravelcardValidatedDTO
    ): ResponseEntity<String> {
        //aggiorno gli utilizzi residui della travelcard
        try {
            travelcardPurchasedService.decreaseUsages(travelcardValidated.travelcardId)
        } catch (e: TicketNotFoundException) {
            return ResponseEntity("Travelcard not found", HttpStatus.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity("This travelcard has no usages remaining", HttpStatus.BAD_REQUEST)
        }
        //se la travelcard non ha più utilizzi disponibili la rimuovo
        try {
            if (!travelcardPurchasedService.hasRemainingUsages(travelcardValidated.travelcardId))
                travelcardPurchasedService.removeTravelcard(travelcardValidated.travelcardId)
        } catch (e: TicketNotFoundException) {
            return ResponseEntity("Travelcard not found", HttpStatus.NOT_FOUND)
        }
        //aggiorno i transit dell'utente aggiungendo la data
        try {
            userDetailsServiceImpl.addTransit(travelcardValidated.username, travelcardValidated.date)
        } catch (e: UserNotFoundException) {
            return ResponseEntity("User not found", HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(HttpStatus.OK)
    }

}