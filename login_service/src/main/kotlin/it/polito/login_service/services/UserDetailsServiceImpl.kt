package it.polito.login_service.services

import it.polito.login_service.entities.User
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.toDTO
import it.polito.login_service.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository


    fun getUserDetails(username: String): UserDTO? {
        var ret: UserDTO? = null
        try {
            ret= userRepository.findUserByUsername(username).get(0).toDTO()
        }catch(e: IndexOutOfBoundsException){
            println(userRepository.findUserByUsername(username).size)
        }
        return ret
    }

    override fun loadUserByUsername(username: String?): User {
        return userRepository.findUserByUsername(username).get(0)
    }

    fun getUserById(uId : Long): UserDTO{
        return userRepository.findById(uId).get().toDTO()
    }

}