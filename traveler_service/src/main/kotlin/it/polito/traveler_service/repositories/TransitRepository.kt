package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.Transit
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransitRepository: CrudRepository<Transit, Long> {
}