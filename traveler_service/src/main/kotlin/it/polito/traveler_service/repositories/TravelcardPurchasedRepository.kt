package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.TravelcardPurchased
import kotlinx.coroutines.flow.Flow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TravelcardPurchasedRepository: CoroutineCrudRepository<TravelcardPurchased, Long> {

    @Query("SELECT t FROM TravelcardPurchased t WHERE t.userDetails.id = ?1")
    fun findAllTravelcards(id:Long): Flow<TravelcardPurchased>

    @Query("UPDATE TravelcardPurchased SET remainingUsages = remainingUsages-1 WHERE sub = ?1")
    suspend fun decreaseRemainingUsages(id:Long)
}