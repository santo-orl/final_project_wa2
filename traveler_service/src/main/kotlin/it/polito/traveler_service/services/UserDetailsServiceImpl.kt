package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.TransitDTO
import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.entities.UserDetailsImpl
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


    fun getUserDetails(username: String): UserDetailsDTO? {
        var ret: UserDetailsDTO? = null
        try {
            ret= userDetailsRepository.findUserDetailsByUserr(username).get(0).toDTO()
        }catch(e: IndexOutOfBoundsException){
            println(userDetailsRepository.findUserDetailsByUserr(username).size)
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
        var userDetails = userDetailsRepository.findUserDetailsByUserr(username).get(0)
        if(userDetails==null)
            insertTraveler(username, userDTO)
        else {
            userDetails.name = userDTO.name
            userDetails.address = userDTO.address
            userDetails.dateOfBirth = userDTO.dateOfBirth
            userDetails.telephoneNumber = userDTO.telephoneNumber
            userDetailsRepository.save(userDetails)
        }
    }


    override fun loadUserByUsername(username: String?): UserDetails {
        return userDetailsRepository.findUserDetailsByUserr(username).get(0)
    }

    fun getTravelers(): List<String>{
        return userDetailsRepository.findAllTravelers()
    }

    fun getUserById(uId : Long): UserDetailsDTO{
        return userDetailsRepository.findById(uId).get().toDTO()
    }

    fun getUserTransits(username: String,from: String, to: String): List<TransitDTO>{
        val user = userDetailsRepository.findUserDetailsByUserr(username)
        user as UserDetailsImpl
        //TODO eccezione se user not found
        return user.transitList!!.filter { transit -> transit.date.isAfter(LocalDateTime.parse(from)) && transit.date.isBefore(LocalDateTime.parse(to)) }
            .map{transit -> transit.toDTO()}
    }

}