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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        var userDetails = UserDetailsImpl()
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        userDetails.userr = username
        userDetailsRepository.save(userDetails)
    }

    fun updateTraveler(username: String, userDTO: UserDetailsDTO) {
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
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        if(user==null) throw UserNotFoundException("User not found")
        return user.transitList!!.filter { transit ->
            transit.date.isAfter(LocalDate.parse(from,formatter)) && transit.date.isBefore(
                LocalDate.parse(to,formatter)
            )
        }
            .map { transit -> transit.toDTO() }
    }

    fun addTransit(username: String, dateString: String) {
        var user = userDetailsRepository.findUserDetailsByUserr(username).getOrNull(0)
        if (user == null) throw UserNotFoundException("user not found")
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDate.parse(dateString, formatter)
        val transit = Transit(date, user)
        transitRepository.save(transit)
    }

}