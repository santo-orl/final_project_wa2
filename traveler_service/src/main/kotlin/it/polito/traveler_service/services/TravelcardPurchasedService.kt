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
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

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

    fun createTravelcard(userId: Long,type: TravelcardPurchased.TravelcardType, zones: String): TravelcardPurchasedDTO {
        var userr = userDetailsRepository.findById(userId).get()
        //imposto il periodo di validità a partire da oggi e fino a una data che dipende dal type
        var validTo: LocalDateTime = when(type){
            TravelcardPurchased.TravelcardType.WEEK -> LocalDateTime.now().plus(1,ChronoUnit.WEEKS)
            TravelcardPurchased.TravelcardType.MONTH -> LocalDateTime.now().plus(1,ChronoUnit.MONTHS)
            TravelcardPurchased.TravelcardType.YEAR -> LocalDateTime.now().plus(1,ChronoUnit.YEARS)
        }
        var travelcard = travelcardPurchasedRepository.save(TravelcardPurchased(type,zones, LocalDateTime.now(),validTo,userr))
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

    fun isExpired(travelcardId: Long): Boolean{
        val travelcardPurchased = travelcardPurchasedRepository.findById(travelcardId).get()
        return LocalDateTime.now().isAfter(travelcardPurchased.validTo)
    }

    //ogni giorno a mezzanotte elimina le travelcard scadute
    @Scheduled(cron = "0 0 0 * * *")
    fun removeExpiredTravelcards(){
        var travelcards = travelcardPurchasedRepository.findAll()
        for(travelcard in travelcards)
            if(isExpired(travelcard.sub))
                travelcardPurchasedRepository.delete(travelcard)
    }

}