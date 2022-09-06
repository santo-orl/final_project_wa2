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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
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

    suspend fun getAllTravelcards(userId: Long): Flow<TravelcardPurchasedDTO> {
        var list: ArrayList<TravelcardPurchasedDTO> = ArrayList()
        var ret = travelcardPurchasedRepository.findAllTravelcards(userId).toList()
        for (travelcardPurchased in ret) {
            list.add(travelcardPurchased.toDTO())
        }
        return list.asFlow()
    }

    suspend fun getTravelcardById(travelcardId: Long, username: String): TravelcardPurchasedDTO {
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(travelcardId)!!
        } catch (e: NullPointerException) {
            throw TicketNotFoundException("Travelcard not found")
        }
        if (!travelcard.userDetails?.userr.equals(username))
            throw UnauthorizedTicketAccessException("This travelcard doesn't belong to the user requesting it")
        return travelcard.toDTO()
    }

    suspend fun createTravelcard(userId: Long,type: TravelcardPurchased.TravelcardType, zones: String): TravelcardPurchasedDTO {
        var userr = userDetailsRepository.findById(userId)!!  //bisogna controllare l'eccezione
        //imposto il periodo di validità a partire da oggi e fino a una data che dipende dal type
        var validTo: LocalDateTime = when(type){
            TravelcardPurchased.TravelcardType.WEEK -> LocalDateTime.now().plus(1,ChronoUnit.WEEKS)
            TravelcardPurchased.TravelcardType.MONTH -> LocalDateTime.now().plus(1,ChronoUnit.MONTHS)
            TravelcardPurchased.TravelcardType.YEAR -> LocalDateTime.now().plus(1,ChronoUnit.YEARS)
        }
        var travelcard = travelcardPurchasedRepository.save(TravelcardPurchased(type,zones, LocalDateTime.now(),validTo,userr))
        return travelcard.toDTO()
    }

    suspend fun removeTravelcard(sub: Long) {
        var travelcard: TravelcardPurchased
        try {
            travelcard = travelcardPurchasedRepository.findById(sub)!!
        } catch (e: NullPointerException) {
            throw TicketNotFoundException("travelcard not found")
        }
        travelcardPurchasedRepository.delete(travelcard)
    }

    suspend fun isExpired(travelcardId: Long): Boolean{
        val travelcardPurchased = travelcardPurchasedRepository.findById(travelcardId)!! //bisogna controllare l'eccezione
        return LocalDateTime.now().isAfter(travelcardPurchased.validTo)
    }

    //ogni giorno a mezzanotte elimina le travelcard scadute
    @Scheduled(cron = "0 0 0 * * *")
    suspend fun removeExpiredTravelcards(){
        var travelcards = travelcardPurchasedRepository.findAll().toList()
        for(travelcard in travelcards)
            if(isExpired(travelcard.sub))
                travelcardPurchasedRepository.delete(travelcard)
    }

}