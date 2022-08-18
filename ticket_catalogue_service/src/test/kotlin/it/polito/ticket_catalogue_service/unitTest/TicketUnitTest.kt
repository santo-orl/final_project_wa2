package it.polito.ticket_catalogue_service.unitTest

import it.polito.ticket_catalogue_service.repository.OrderRepository
import it.polito.ticket_catalogue_service.repository.TicketCatRepository
import it.polito.ticket_catalogue_service.service.OrderService
import it.polito.ticket_catalogue_service.service.TicketCatService
import kotlinx.coroutines.flow.toList
import org.junit.Test
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

        init {
            ticketCatService = Mockito.mock(TicketCatService::class.java)
            ticketCatRepository = Mockito.mock(TicketCatRepository::class.java)
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

        //vedere come testare askForPayment, COME SI FA ??????!!!
    }
}