package it.polito.login_service.dtos

import it.polito.login_service.entities.User

data class UserLoginDTO (
    val username: String,
    val password: String
)

fun User.toUserLoginDTO(): UserLoginDTO{
    return UserLoginDTO(this.userrname,this.passsword)
}
