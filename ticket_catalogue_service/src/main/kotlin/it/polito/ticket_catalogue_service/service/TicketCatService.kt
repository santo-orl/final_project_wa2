package it.polito.ticket_catalogue_service.service

import it.polito.ticket_catalogue_service.dtos.*
import it.polito.ticket_catalogue_service.entities.Ticket
import it.polito.ticket_catalogue_service.entities.Travelcard
import it.polito.ticket_catalogue_service.exceptions.InvalidTicketException
import it.polito.ticket_catalogue_service.exceptions.NullTicketException
import it.polito.ticket_catalogue_service.exceptions.TicketNotFoundException
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.repository.TravelcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Service
class TicketCatService {

    @Autowired
    lateinit var ticketCatRepository: TicketCatRepository

    @Autowired
    lateinit var kafkaPaymentTemplate: KafkaTemplate<String, PaymentRequestDTO>

    @Autowired
    lateinit var travelcardRepository: TravelcardRepository

    @Value("\${travelerServiceUrl}")
    lateinit var travelerServiceUrl: String

    @Value("\${topics.payment-request-topic.name}")
    lateinit var paymentRequestTopic: String


    var client: WebClient = WebClient.create()

    //TODO la comunicazione con traveler service va fatta con kafka
    suspend fun isValid(jwt: String, ticketId: Long): Boolean {
        val ticket = ticketCatRepository.findById(ticketId)
        var userDetails: UserDetailsDTO = UserDetailsDTO(" ", " ", " ", " ", " ")
        if (ticket != null) {
            //se non ha restrizioni allora è valido
            if (ticket.maxAge == -1 || ticket.minAge == -1) {
                return true
            }
            //altrimenti prendo le info dell'utente da TravelerService per controllare che sia valido
            try {
                userDetails = client.get()
                    .uri(travelerServiceUrl + "/my/profile")
                    .header("Authorization", jwt)
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .awaitBody<UserDetailsDTO>()
            } catch (e: InvalidTicketException) { //retrieve() lancia un'eccezione se riceve una risposta http che non abbia status 2xx
                println("Ticket is not valid")
                return false
            }
            //controllo che l'età dell'utente rientri nelle restrizioni
            return ageInRange(userDetails.dateOfBirth, ticket.minAge, ticket.maxAge) //TODO fare la funzione

        } else { //se ticket è null eccezione da catturare nel controller
            throw NullTicketException("Ticket is null")
        }
    }//isValid

    suspend fun askForPayment(request: ShopRequestDTO, orderId: Long, username: String, jwt: String) {
        val ticket = ticketCatRepository.findById(request.ticketId)
        var totalPrice = 0F
        if (ticket != null) {
            totalPrice = request.nTickets * ticket.price
        }
        //mando le info per il pagamento a PaymentService con Kafka
        val paymentRequest = PaymentRequestDTO(
            request.cardHolder,
            request.creditCardNumber.toString(),
            request.expDate,
            request.cvv.toString(),
            orderId,
            totalPrice,
            username,
            jwt
        )
        runBlocking { //TODO si può togliere runBlocking?
            kafkaPaymentTemplate.send(paymentRequestTopic, paymentRequest)
        }
        //ricevo la risposta sul listener in KafkaListenerService
    }

    fun ageInRange(dateOfBirth: String, minAge: Int?, maxAge: Int?): Boolean {

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val birthDate = LocalDate.parse(dateOfBirth, formatter)
        val now = LocalDate.now()
        val age = ChronoUnit.YEARS.between(birthDate, now)

        if (minAge != null && maxAge != null) {
            if (age in minAge..maxAge) {
                return true
            }
        }
        return false
    }


    suspend fun addNewTicket(newTicketDTO: TicketDTO): TicketDTO {
        return ticketCatRepository.save(
            Ticket(
                null,
                newTicketDTO.ticketType,
                newTicketDTO.price,
                newTicketDTO.minAge,
                newTicketDTO.maxAge,
                newTicketDTO.zid,
                newTicketDTO.validFrom
            )
        ).toDTO()
    }

    fun getTickets(): Flow<Ticket> {
        return ticketCatRepository.getTickets()
    }

    suspend fun removeTicket(ticketId: Long) {
        val ticket = ticketCatRepository.findById(ticketId)
        if (ticket != null)
            ticketCatRepository.delete(ticket)
    }

    suspend fun updateTicket(ticketId: Long, newTicket: TicketDTO): TicketDTO {
        val ticket = ticketCatRepository.findById(ticketId)
        if (ticket == null) throw TicketNotFoundException("Ticket with id $ticketId does not exist")
        ticketCatRepository.delete(ticket)
        return ticketCatRepository.save(
            Ticket(
                null,
                newTicket.ticketType,
                newTicket.price,
                newTicket.minAge,
                newTicket.maxAge,
                newTicket.zid,
                newTicket.validFrom
            )
        ).toDTO()
    }

    fun getTravelcards(): Flow<Travelcard> {
        return travelcardRepository.findAll()
    }

}