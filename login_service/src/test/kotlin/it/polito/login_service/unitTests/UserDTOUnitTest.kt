package it.polito.login_service.unitTests

import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.toDTO
import it.polito.login_service.entities.User
import org.junit.jupiter.api.Test

class UserDTOUnitTest() {


    @Test
    //check fun toDTO: user and userDTO should have the same username
    fun usernameTest(){
        val user = User(0,"username","password","email","status")
        val userDTO: UserDTO = user.toDTO()
        assert(userDTO.username==user.username)
    }

    @Test
    //check fun toDTO: user and userDTO should have the same password
    fun passwordTest(){
        val user = User(0,"username","password","email","status")
        val userDTO: UserDTO = user.toDTO()
        assert(userDTO.password==user.password)
    }

    @Test
    //check fun toDTO: user and userDTO should have the same email
    fun emailTest(){
        val user = User(0,"username","password","email","status")
        val userDTO: UserDTO = user.toDTO()
        assert(userDTO.email==user.email)
    }


}