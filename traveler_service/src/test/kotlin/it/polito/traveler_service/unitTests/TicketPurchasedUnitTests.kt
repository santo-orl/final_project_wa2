package it.polito.traveler_service.unitTests

import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.repositories.TicketPurchasedRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import it.polito.traveler_service.services.TicketPurchasedService
import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner

class TicketPurchasedUnitTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class TicketPurchasedTest{

        lateinit var ticketPurchasedService : TicketPurchasedService
        lateinit var ticketPurchasedRepository: TicketPurchasedRepository
        lateinit var ticketPurchasedDTO: TicketPurchasedDTO
        lateinit var userDetailsRepository: UserDetailsRepository
        lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

        init{
            ticketPurchasedService = Mockito.mock(TicketPurchasedService::class.java)
            ticketPurchasedRepository = Mockito.mock(TicketPurchasedRepository::class.java)
            ticketPurchasedDTO = Mockito.mock(TicketPurchasedDTO::class.java)
            userDetailsRepository = Mockito.mock(UserDetailsRepository::class.java)
            userDetailsServiceImpl = Mockito.mock(UserDetailsServiceImpl::class.java)
        }


        @Test
        //suppone sia presente il tipo di ticket che voglio controllare
        fun checkGetsAllTickets(){
            //suppongo id 0 sia presente
            var ret = ticketPurchasedService.getAllTickets(0)
            assert(ret.isNotEmpty())
        }

        @Test
        //suppone non sia presente il tipo di ticket che sto cercando
        fun checkGetsAllTicketsNone(){
            //suppongo id 0 non sia presente
            var ret = ticketPurchasedService.getAllTickets(0)
            assert(ret.isEmpty())
        }

        @Test
        fun checkZoneInvalidCreateTicket(){
            Assertions.assertThrows(Exception::class.java){
                ticketPurchasedService.createTicket("",0,"2022-08-09 10:08","DAILY")
            }
        }

        @Test
        fun checkIdInvalidCreateTicket(){
            //suppongo id 100 non presente
            Assertions.assertThrows(Exception::class.java){
                ticketPurchasedService.createTicket("1",100,"2022-08-09 10:08","DAILY")
            }
        }

        @Test
        fun checkValidFromInvalidCreateTicket(){
            Assertions.assertThrows(Exception::class.java){
                ticketPurchasedService.createTicket("1",1,"2042-08-09 10:08","DAILY")
            }
        }

        @Test
        fun checkTypeInvalidCreateTicket(){
            Assertions.assertThrows(Exception::class.java){
                ticketPurchasedService.createTicket("1",1,"2022-08-09 10:08","")
            }
        }



    }
}