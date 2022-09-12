package it.polito.traveler_service.unitTests

import it.polito.traveler_service.repositories.TransitRepository
import it.polito.traveler_service.services.TransitService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

class TransitUnitTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class transitTest{

        lateinit var transitService: TransitService
        lateinit var transitRepository: TransitRepository

        init{
            transitService = Mockito.mock(TransitService::class.java)
            transitRepository = Mockito.mock(TransitRepository::class.java)
        }

        @Test
        fun checkRange(from: String, to: String){
            assert(LocalDateTime.parse(from).isBefore(LocalDateTime.parse(to)))
        }
    }
}