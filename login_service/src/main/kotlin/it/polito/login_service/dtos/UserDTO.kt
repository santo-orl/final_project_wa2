package it.polito.login_service.dtos

import it.polito.login_service.entities.User

data class UserDTO(
        val username: String,
        val password: String,
        val email: String,
        var id: Long = 0,
        var status: String = "inactive"
)

fun User.toDTO(): UserDTO{
        return UserDTO(this.userrname,this.passsword,email,id,status)
}