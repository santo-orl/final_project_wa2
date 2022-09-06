package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.Transit
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransitRepository: CoroutineCrudRepository<Transit, Long> {
}