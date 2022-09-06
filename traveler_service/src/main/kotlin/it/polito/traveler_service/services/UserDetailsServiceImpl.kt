package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.Transit
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.exceptions.UserNotFoundException
import it.polito.traveler_service.repositories.TransitRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var transitRepository: TransitRepository


    suspend fun getUserDetails(username: String): UserDetailsDTO {
        var ret: UserDetailsDTO
        try {
            ret = userDetailsRepository.findUserDetailsByUserr(username).first().toDTO()
        } catch (e: IndexOutOfBoundsException) {
            throw UserNotFoundException("User not found")
        }
        return ret
    }

    suspend fun insertTraveler(username: String, userDTO: UserDetailsDTO) {
        //TODO check correttezza parametri
        var userDetails = UserDetailsImpl()
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        userDetails.userr = username
        userDetailsRepository.save(userDetails)
    }

    suspend fun updateTraveler(username: String, userDTO: UserDetailsDTO) {
        //TODO check correttezza parametri
        var userDetails: UserDetailsImpl
        try{
            userDetails = userDetailsRepository.findUserDetailsByUserr(username).first()
        }
        catch(e: NoSuchElementException){
            throw UserNotFoundException("User not found")
        }

        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        userDetailsRepository.save(userDetails)
    }


    override fun loadUserByUsername(username: String?): UserDetails {
        var userDetails: UserDetailsImpl
        runBlocking(){
            userDetails = userDetailsRepository.findUserDetailsByUserr(username).first()
        }
        return userDetails
    }

    fun getTravelers(): Flow<String> {
        return userDetailsRepository.findAllTravelers()
    }

    suspend fun getUserById(uId: Long): UserDetailsDTO {
        try {
            return userDetailsRepository.findById(uId)!!.toDTO()  //.get().toDTO()
        } catch (e: NullPointerException) {
            throw UserNotFoundException("User not found")
        }
    }

    suspend fun getUserTransits(username: String, from: String, to: String): Flow<TransitDTO> {
        val user = userDetailsRepository.findUserDetailsByUserr(username).toList().getOrNull(0)
        if(user==null) throw UserNotFoundException("User not found")
        return user.transitList!!.asFlow().filter { transit ->
            transit.date.isAfter(LocalDateTime.parse(from)) && transit.date.isBefore(
                LocalDateTime.parse(to)
            )
        }
            .map { transit -> transit.toDTO() }
    }

    suspend fun addTransit(username: String, date: LocalDateTime) {
        var user = userDetailsRepository.findUserDetailsByUserr(username).toList().getOrNull(0)
        if (user == null) throw UserNotFoundException("user not found")
        val transit = Transit(date, user)
        transitRepository.save(transit)
        //TODO aggiungendo il transit alla tabella si aggiorna anche la relazione one to many lato user?
    }

}