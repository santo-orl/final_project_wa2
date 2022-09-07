package it.polito.ticket_catalogue_service.unitTest

import it.polito.ticket_catalogue_service.dtos.TravelcardDTO
import it.polito.ticket_catalogue_service.entities.TravelcardType
import it.polito.ticket_catalogue_service.repository.TravelcardRepository
import it.polito.ticket_catalogue_service.service.TravelcardService
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import javax.swing.text.html.parser.Entity

class TravelCardUnitTest {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)

    class TravelCardTest{

        lateinit var travelCardService: TravelcardService
        lateinit var travelCardRepository: TravelcardRepository

        init {
            travelCardService = Mockito.mock(TravelcardService::class.java)
            travelCardRepository = Mockito.mock(TravelcardRepository::class.java)
        }

        //testare askforpayment

        //controllare se è stato aggiunto
        @Test
        suspend fun checkAddTravelCard(){
            travelCardService.addNewTravelcard(
                TravelcardDTO(TravelcardType.WEEK, 5f, 18, 25, "A")
            )

            val travelCard = travelCardRepository.findById(0L)
            assert(!travelCard!!.equals(null))
        }

        //controllare se i campi sono ok
        @Test
        suspend fun checkAddTravelCardBadZone(){
            val ret = travelCardService.addNewTravelcard(
                TravelcardDTO(TravelcardType.WEEK, 5f, 18, 25, "")
            )

            assert(ret.zid.isEmpty())
        }

        //controllare se min e max sono numeri positivi e se min è maggiore di max
        @Test
        suspend fun checkAddTravelCardBadAge(){
            val ret = travelCardService.addNewTravelcard(
                TravelcardDTO(TravelcardType.WEEK, 5f, -1, 25, "A")
            )
            if(ret.minAge!! < 0){
                assert(false)
            }
            else if(ret.maxAge!! < 0) {
                assert(false)
            }
            else if(ret.maxAge!! < ret.minAge!!){
                assert(false)
            }

            assert(true)
        }

        //controllo che il prezzo non sia negativo
        @Test
        suspend fun checkAddTravelCardBadPrice(){

            val ret = travelCardService.addNewTravelcard(
                TravelcardDTO(TravelcardType.WEEK, -5f, 1, 25, "A")
            )

            if(ret.price < 0){
                assert(false)
            }

            assert(true)
        }

        @Test
        suspend fun checkRemoveTravelCard(){
            var ret = travelCardService.removeTravelcard(0L)
            assert(false)
        }

        //TODO
        @Test
        suspend fun checkUpdateTravelCard(){


        }

        /* //per l'update
        @Test
        void test() {
              // Arrange
               ArgumentCaptor<Entity> entityArgumentCaptor = ArgumentCaptor.forClass(Entity.class);
                InputDto inputDto = prepareTestInput();

               // Act
                ResponseDto responseDto = service.update(inputDto);
                verify(repositoryMock, times(1)).save(entityArgumentCaptor.capture());

                // Assert
                Entity savedEntity = argumentCaptor.getValue();
                assertEquals(input.getVariable1(), savedEntity.getVariable1());
                // .....
                // compare ResponseDto and InputDto too
}
         */



    }



}