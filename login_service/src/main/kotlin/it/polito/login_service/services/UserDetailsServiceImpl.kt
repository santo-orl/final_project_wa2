package it.polito.login_service.services

import it.polito.login_service.entities.User
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.toDTO
import it.polito.login_service.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository


    suspend fun getUserDetails(username: String): UserDTO? {
        var ret: UserDTO? = null
        try {
            ret = userRepository.findUserByUsername(username).first().toDTO()
        }catch(e: IndexOutOfBoundsException){
            println(userRepository.findUserByUsername(username).toList().size)
        }
        return ret
    }

    /*fun updateTraveler(username: String, userDTO: UserDetailsDTO) {
        val userDetails = userDetailsRepository.findUserDetailsByUserr(username).get(0)
        //traveler dovrebbe essere managed, quindi i cambiamenti che faccio potranno passare al db
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.dateOfBirth = userDTO.dateOfBirth
        userDetails.telephoneNumber = userDTO.telephoneNumber
        println(userDetails)
        userDetailsRepository.save(userDetails)
    }*/


    override fun loadUserByUsername(username: String?): User {
        var user: User = User()
        runBlocking {
            user = userRepository.findUserByUsername(username).first()
        }
        return user
    }
/*
    fun getTravelers(): List<String>{
        return userRepository.findAllTravelers()
    }*/

    suspend fun getUserById(uId : Long): UserDTO{
        return userRepository.findById(uId)!!.toDTO()
    }

}