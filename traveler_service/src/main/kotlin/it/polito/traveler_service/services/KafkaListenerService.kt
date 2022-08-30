package it.polito.traveler_service.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.traveler_service.dtos.CreateTicketsDTO
import it.polito.traveler_service.dtos.CreateTravelcardDTO
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaListenerService {

    @Autowired
    lateinit var ticketPurchasedService: TicketPurchasedService
    @Autowired
    lateinit var travelcardPurchasedService: TravelcardPurchasedService
    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    //riceve da ticket_purchased_service la richiesta di aggiungere al db dei ticket purchased
    //uno o più ticket il cui acquisto è andato a buon fine
    @KafkaListener(topics = ["TicketPurchasedTopic"], groupId = "\${ticket.topic.group.id}",containerFactory = "ticketKafkaListenerContainerFactory")
    suspend fun createTicketPurchased(createTicketPurchasedString: String) {
        //TODO gestione errori
        val createTicketsPurchased: CreateTicketsDTO =
            ObjectMapper().readValue(createTicketPurchasedString, CreateTicketsDTO::class.java)
        val user = userDetailsRepository.findUserDetailsByUserr(createTicketsPurchased.username).get(0)
        for (i in 0 until createTicketsPurchased.quantity) {
            var ticket = ticketPurchasedService.createTicket(
                createTicketsPurchased.zones,
                user.id,
                createTicketsPurchased.validFrom,
                createTicketsPurchased.type
            )
        }
    }

    //riceve da ticket_purchased_service la richiesta di aggiungere al db delle travelcard purchased
    //una travelcard il cui acquisto è andato a buon fine
    @KafkaListener(topics = ["TravelcardPurchasedTopic"], groupId = "\${travelcard.topic.group.id}",containerFactory = "travelcardKafkaListenerContainerFactory")
    suspend fun createTravelcardPurchased(createTravelcardPurchasedString: String) {
        //TODO gestione errori
        val createTravelcardPurchased: CreateTravelcardDTO =
            ObjectMapper().readValue(createTravelcardPurchasedString, CreateTravelcardDTO::class.java)
        val user = userDetailsRepository.findUserDetailsByUserr(createTravelcardPurchased.username).get(0)
            var travelcard = travelcardPurchasedService.createTravelcard(
                user.id,
                createTravelcardPurchased.type,
                createTravelcardPurchased.zones
            )
    }


}