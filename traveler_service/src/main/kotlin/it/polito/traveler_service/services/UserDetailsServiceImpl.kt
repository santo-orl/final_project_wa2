package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.Transit
import it.polito.traveler_service.entities.UserDetailsImpl
import it.polito.traveler_service.exceptions.UserNotFoundException
import it.polito.traveler_service.repositories.TransitRepository
import it.polito.traveler_service.repositories.UserDetailsRepository
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


    fun getUserDetails(username: String): UserDetailsDTO {
        var ret: UserDetailsDTO
        try {
            ret = userDetailsRepository.findUserDetailsByUserr(username).get(0).toDTO()
        } catch (e: IndexOutOfBoundsException) {
            throw UserNotFoundException("User not found")
        }
        return ret
    }

    fun insertTraveler(username: String, userDTO: UserDetailsDTO) {
        //TODO check correttezza parametri
        var userDetails = UserDetailsImpl()
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        userDetails.userr = username
        userDetailsRepository.save(userDetails)
    }

    fun updateTraveler(username: String, userDTO: UserDetailsDTO) {
        //TODO check correttezza parametri
        var userDetails = userDetailsRepository.findUserDetailsByUserr(username).getOrNull(0)
        if (userDetails == null)
            throw UserNotFoundException("User not found")
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        userDetailsRepository.save(userDetails)
    }


    override fun loadUserByUsername(username: String?): UserDetails {
        return userDetailsRepository.findUserDetailsByUserr(username).get(0)
    }

    fun getTravelers(): List<String> {
        return userDetailsRepository.findAllTravelers()
    }

    fun getUserById(uId: Long): UserDetailsDTO {
        try {
            return userDetailsRepository.findById(uId).get().toDTO()
        } catch (e: NoSuchElementException) {
            throw UserNotFoundException("User not found")
        }
    }

    fun getUserTransits(username: String, from: String, to: String): List<TransitDTO> {
        val user = userDetailsRepository.findUserDetailsByUserr(username).getOrNull(0)
        if(user==null) throw UserNotFoundException("User not found")
        return user.transitList!!.filter { transit ->
            transit.date.isAfter(LocalDateTime.parse(from)) && transit.date.isBefore(
                LocalDateTime.parse(to)
            )
        }
            .map { transit -> transit.toDTO() }
    }

    fun addTransit(username: String, date: LocalDateTime) {
        var user = userDetailsRepository.findUserDetailsByUserr(username).getOrNull(0)
        if (user == null) throw UserNotFoundException("user not found")
        val transit = Transit(date, user)
        transitRepository.save(transit)
        //TODO aggiungendo il transit alla tabella si aggiorna anche la relazione one to many lato user?
    }

}