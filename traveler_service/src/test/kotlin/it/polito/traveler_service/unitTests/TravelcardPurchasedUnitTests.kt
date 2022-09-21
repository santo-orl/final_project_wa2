package it.polito.traveler_service.unitTests

import it.polito.traveler_service.entities.TravelcardPurchased
import it.polito.traveler_service.repositories.TravelcardPurchasedRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import it.polito.traveler_service.services.TravelcardPurchasedService
import org.hibernate.cfg.Environment
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.test.context.junit4.SpringRunner
import javax.sql.DataSource

@DataJpaTest
class TravelcardPurchasedUnitTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class travelcardTests{

        lateinit var travelcardPurchasedService: TravelcardPurchasedService
        lateinit var travelcardPurchasedRepository: TravelcardPurchasedRepository
        lateinit var userDetailsRepo: UserDetailsRepository

        init{
            travelcardPurchasedService = TravelcardPurchasedService()
            travelcardPurchasedRepository = Mockito.mock(TravelcardPurchasedRepository::class.java)
            userDetailsRepo = Mockito.mock(UserDetailsRepository::class.java)
        }

        @Test
        fun checkGetAllTravelcards(){
            assert(travelcardPurchasedService.getAllTravelcards(0L).toList().isNotEmpty())
        }

        @Test
        fun checkGetTravelcardByIdNotExist(){
            //supposing it does not exist
            Assertions.assertThrows(NoSuchElementException::class.java){
                travelcardPurchasedService.getTravelcardById(0L,"username")
            }
        }

        @Test
        fun checkAddTravelCard(){
            travelcardPurchasedService.createTravelcard(0L,TravelcardPurchased.TravelcardType.WEEK,"A")
            val travelCard = travelcardPurchasedRepository.findById(0L)
            assert(!travelCard!!.equals(null))
        }

        @Test
        fun checkRemoveTravelCard(){
            travelcardPurchasedService.createTravelcard(0L,TravelcardPurchased.TravelcardType.WEEK,"A")
            var ret = travelcardPurchasedService.removeTravelcard(0L)
            assert(travelcardPurchasedRepository.findAllTravelcards(0L).isEmpty())
        }

    }
}
