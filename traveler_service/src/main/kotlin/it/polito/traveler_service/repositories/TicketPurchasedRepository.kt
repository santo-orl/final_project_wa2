package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.TicketPurchased
import kotlinx.coroutines.flow.Flow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketPurchasedRepository: CoroutineCrudRepository<TicketPurchased, Long> {

    @Query("SELECT t FROM TicketPurchased t WHERE t.userDetails.id = ?1")
    fun findAllTickets(id:Long): Flow<TicketPurchased>
}