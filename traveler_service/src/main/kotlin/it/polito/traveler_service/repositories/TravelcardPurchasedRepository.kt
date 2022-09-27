package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.TravelcardPurchased
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TravelcardPurchasedRepository: CrudRepository<TravelcardPurchased, Long> {

    @Query("SELECT t FROM TravelcardPurchased t WHERE t.userDetails.id = ?1")
    fun findAllTravelcards(id:Long): List<TravelcardPurchased>

    @Query("SELECT t FROM TravelcardPurchased t WHERE t.sub = ?1")
    fun findTravelcardPurchasedBySub(sub: Long): TravelcardPurchased

}