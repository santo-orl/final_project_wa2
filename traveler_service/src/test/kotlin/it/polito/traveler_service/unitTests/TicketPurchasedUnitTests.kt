package it.polito.traveler_service.unitTests

import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.entities.UserDetailsImpl
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TicketPurchasedUnitTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class TicketPurchasedTest{

        @Autowired
        lateinit var ticketPurchasedRepository : TicketPurchasedRepository
        lateinit var ticketPurchasedService : TicketPurchasedService

        @Test
//suppone sia presente il tipo di ticket che voglio controllare
        fun checkGetsAllTickets(){
            //suppongo id 0 sia presente
           ticketPurchasedRepository.save(TicketPurchased(LocalDateTime.now(),"1",UserDetailsImpl(),LocalDateTime.now(),"Daily"))
        println("salvato")
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


/********************************/

    }


}