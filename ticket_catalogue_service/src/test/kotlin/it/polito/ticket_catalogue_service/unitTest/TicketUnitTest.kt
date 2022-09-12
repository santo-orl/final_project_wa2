package it.polito.ticket_catalogue_service.unitTest

import it.polito.ticket_catalogue_service.dtos.TicketDTO
import it.polito.ticket_catalogue_service.dtos.TravelcardDTO
import it.polito.ticket_catalogue_service.entities.TravelcardType
import it.polito.ticket_catalogue_service.repository.OrderRepository
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.repository.TravelcardRepository
import it.polito.ticket_catalogue_service.service.OrderService
import it.polito.ticket_catalogue_service.service.TicketCatService
import it.polito.ticket_catalogue_service.service.TravelcardService
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner

class TicketUnitTest {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)

    class TicketCatTest {

        lateinit var ticketCatService: TicketCatService
        lateinit var ticketCatRepository: TicketCatRepository
        lateinit var travelCardRepository : TravelcardRepository
        lateinit var travelCardService: TravelcardService

        init {
            ticketCatService = Mockito.mock(TicketCatService::class.java)
            ticketCatRepository = Mockito.mock(TicketCatRepository::class.java)
            travelCardRepository = Mockito.mock(TravelcardRepository::class.java)
            travelCardService = Mockito.mock(TravelcardService::class.java)
        }

        //TODO: isValid ????

        @Test
        suspend fun checkAddTicket(){
            ticketCatService.addNewTicket(
                TicketDTO("DAILY",5f, 18, 25, "A", "07-09-2022"))

            var ret = ticketCatRepository.findById(0L)

            assert(!ret!!.equals(null))   //da controllare!!!!
        }

        @Test
        suspend fun checkUpdateTicket(){
            //aggiungo ticket temporaneo
            val tmp = ticketCatService.addNewTicket(TicketDTO("DAILY",5f, 18, 25, "A", "07-09-2022"))
            //creo ticket da controllare con quello aggiornato
            val tmp2 = TicketDTO("DAILY",5f, 8, 25, "A", "07-09-2022")
            //provo aggiornamento
            ticketCatService.updateTicket(0L,tmp2)
            //controllo che l'update sia andata bene
            assert(ticketCatRepository.findById(0L)!!.equals(tmp2))
        }

        @Test
        suspend fun checkGetAllTickets() {
            var ret = ticketCatService.getTickets()
            assert(ret.toList().isNotEmpty())
        }

        @Test
        suspend fun checkRemoveTicket() {
            //se ticket non c'è
            var ret = ticketCatService.removeTicket(0L)
            assert(ret == null)
        }

        //TODO: vedere come testare askForPayment, COME SI FA ??????!!!

        //TODO: testare getAllTravelCards????
        @Test
        suspend fun checkGetAllTravelCards(){
            travelCardService.addNewTravelcard(TravelcardDTO(TravelcardType.MONTH,0F,18,25,"A"))
            assert(travelCardRepository.findAll().toList().isNotEmpty())
        }
    }
}