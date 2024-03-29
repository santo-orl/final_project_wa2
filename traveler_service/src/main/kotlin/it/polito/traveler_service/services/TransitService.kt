package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.repositories.TransitRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransitService {

    @Autowired
    lateinit var transitRepository: TransitRepository

    fun getInRange(from: String, to: String): Flow<TransitDTO> {
        var transits = transitRepository.findAll()
        return transits.filter{ transit -> transit.date.isAfter(LocalDateTime.parse(from)) && transit.date.isBefore(LocalDateTime.parse(to)) }
            .map{transit -> transit.toDTO()}
    }

}