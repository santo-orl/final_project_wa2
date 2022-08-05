package it.polito.traveler_service.services

import it.polito.traveler_service.dtos.UserDetailsDTO
import it.polito.traveler_service.dtos.toDTO
import it.polito.traveler_service.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

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

    fun updateTraveler(username: String, userDTO: UserDetailsDTO) {
        val userDetails = userDetailsRepository.findUserDetailsByUserr(username).get(0)
        //traveler dovrebbe essere managed, quindi i cambiamenti che faccio potranno passare al db
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        println(userDetails)
        userDetailsRepository.save(userDetails)
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

}