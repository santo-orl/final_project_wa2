package it.polito.traveler_service.unitTests

import it.polito.traveler_service.entities.TravelcardPurchased
import it.polito.traveler_service.repositories.TravelcardPurchasedRepository
import it.polito.traveler_service.services.TravelcardPurchasedService
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner

class TravelcardPurchasedUnitTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class travelcardTests{

        lateinit var travelcardPurchasedService: TravelcardPurchasedService
        lateinit var travelcardPurchasedRepository: TravelcardPurchasedRepository

        init{
            travelcardPurchasedService = Mockito.mock(TravelcardPurchasedService::class.java)
            travelcardPurchasedRepository = Mockito.mock(TravelcardPurchasedRepository::class.java)
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
        suspend fun checkAddTravelCard(){
            travelcardPurchasedService.createTravelcard(0L,TravelcardPurchased.TravelcardType.WEEK,"A")
            val travelCard = travelcardPurchasedRepository.findById(0L)
            assert(!travelCard!!.equals(null))
        }

        @Test
        suspend fun checkRemoveTravelCard(){
            var ret = travelcardPurchasedService.removeTravelcard(0L)
            assert(false)
        }

    }
}
