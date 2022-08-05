package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.repositories.TicketPurchasedRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TicketPurchasedService {

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository


    fun getAllTickets(id:Long):List<TicketPurchasedDTO>{

        var list: ArrayList<TicketPurchasedDTO> = ArrayList()
        var ret = ticketPurchasedRepository.findAllTickets(id)

        for(ticketPurchased in ret){
            list.add(ticketPurchased.toDTO())
        }

        return list
    }

    fun createTicket(zones:String, id: Long, validFrom: String, type: String): TicketPurchasedDTO {
        var userr = userDetailsRepository.findById(id).get()
        var ticket = TicketPurchased(LocalDateTime.now(), zones, userr, LocalDateTime.parse(validFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), type)
        ticketPurchasedRepository.save(ticket)
        return ticket.toDTO()
    }
}