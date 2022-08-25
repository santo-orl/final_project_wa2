package it.polito.traveler_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.exceptions.TicketNotFoundException
import it.polito.traveler_service.exceptions.UnauthorizedTicketAccessException
import it.polito.traveler_service.repositories.TicketPurchasedRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TicketPurchasedService {

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository


    fun getAllTickets(userId: Long): List<TicketPurchasedDTO> {
        var list: ArrayList<TicketPurchasedDTO> = ArrayList()
        var ret = ticketPurchasedRepository.findAllTickets(userId)
        for (ticketPurchased in ret) {
            list.add(ticketPurchased.toDTO())
        }
        return list
    }

    fun createTicket(zones: String, id: Long, validFrom: String, type: String): TicketPurchasedDTO {
        var userr = userDetailsRepository.findById(id).get()
        var ticket = TicketPurchased(
            LocalDateTime.now(),
            zones,
            userr,
            LocalDateTime.parse(validFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            type
        )
        ticketPurchasedRepository.save(ticket)
        return ticket.toDTO()
    }

    fun removeTicket(sub: Long) {
        var ticket: TicketPurchased
        try {
            ticket = ticketPurchasedRepository.findById(sub).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("ticket not found")
        }
        ticketPurchasedRepository.delete(ticket)
    }

    fun getTicketById(ticketId: Long, username: String): TicketPurchasedDTO {
        var ticket: TicketPurchased
        try {
            ticket = ticketPurchasedRepository.findById(ticketId).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("Ticket not found")
        }
        if (!ticket.userDetails?.userr.equals(username))
            throw UnauthorizedTicketAccessException("This ticket doesn't belong to the user requesting it")
        return ticket.toDTO()
    }

}