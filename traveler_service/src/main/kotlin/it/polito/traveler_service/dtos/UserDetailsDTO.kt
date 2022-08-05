package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.UserDetailsImpl


data class UserDetailsDTO(
        val name: String,
        val address: String,
        val dateOfBirth: String,
        val telephoneNumber: String,
        val userr: String

)

fun UserDetailsImpl.toDTO(): UserDetailsDTO {
    return UserDetailsDTO(this.name,this.address,this.dateOfBirth,this.telephoneNumber,this.userr)
}
