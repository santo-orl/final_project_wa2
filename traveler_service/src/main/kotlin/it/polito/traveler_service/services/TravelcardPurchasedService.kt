package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TicketPurchasedDTO
import it.polito.traveler_service.dtos.TravelcardPurchasedDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.entities.TravelcardPurchased
import it.polito.traveler_service.exceptions.TicketNotFoundException
import it.polito.traveler_service.exceptions.UnauthorizedTicketAccessException
import it.polito.traveler_service.repositories.TravelcardPurchasedRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TravelcardPurchasedService {

    @Autowired
    lateinit var travelcardPurchasedRepository: TravelcardPurchasedRepository
    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    fun getAllTravelcards(userId: Long): List<TravelcardPurchasedDTO> {
        var list: ArrayList<TravelcardPurchasedDTO> = ArrayList()
        var ret = travelcardPurchasedRepository.findAllTravelcards(userId)
        for (travelcardPurchased in ret) {
            list.add(travelcardPurchased.toDTO())
        }
        return list
    }

    fun getTravelcardById(travelcardId: Long, username: String): TravelcardPurchasedDTO {
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(travelcardId).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("Travelcard not found")
        }
        if (!travelcard.userDetails?.userr.equals(username))
            throw UnauthorizedTicketAccessException("This travelcard doesn't belong to the user requesting it")
        return travelcard.toDTO()
    }

    fun createTravelcard(zones: String, id: Long, validFrom: String, type: String, remainingUsages: Int): TravelcardPurchasedDTO {
        var userr = userDetailsRepository.findById(id).get()
        var travelcard = TravelcardPurchased(
            LocalDateTime.now(),
            zones,
            userr,
            LocalDateTime.parse(validFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            type,
            remainingUsages
        )
        travelcardPurchasedRepository.save(travelcard)
        return travelcard.toDTO()
    }

    fun removeTravelcard(sub: Long) {
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(sub).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("travelcard not found")
        }
        travelcardPurchasedRepository.delete(travelcard)
    }

    fun decreaseUsages(travelcardId: Long){
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(travelcardId).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("Travelcard not found")
        }
        if(travelcard.remainingUsages <= 0) throw IllegalArgumentException("This travelcard has no remaining usages")
        travelcardPurchasedRepository.decreaseRemainingUsages(travelcardId)
    }

    fun hasRemainingUsages(travelcardId: Long): Boolean{
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(travelcardId).get()
        } catch (e: NoSuchElementException) {
            throw TicketNotFoundException("Travelcard not found")
        }
        return travelcard.remainingUsages!=0
    }

}