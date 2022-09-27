package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.repositories.TransitRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TransitService {

    @Autowired
    lateinit var transitRepository: TransitRepository

    fun getInRange(from: String, to: String): List<TransitDTO> {
        var transits = transitRepository.findAll()
        return transits.filter{ transit -> transit.date.isAfter(LocalDate.parse(from)) && transit.date.isBefore(LocalDate.parse(to)) }
            .map{transit -> transit.toDTO()}
    }

}