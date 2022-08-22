package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.Transit
import java.time.LocalDateTime

data class TransitDTO(
    val id: Long,
    val date: LocalDateTime,
    val username: String
)

fun Transit.toDTO(): TransitDTO {
    return TransitDTO(id,date,userDetails!!.username)
}