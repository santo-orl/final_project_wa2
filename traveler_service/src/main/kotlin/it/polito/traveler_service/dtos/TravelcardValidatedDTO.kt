package it.polito.traveler_service.dtos

import java.time.LocalDateTime

data class TravelcardValidatedDTO(
    val travelcardId: Long,
    val username: String,
    val date: LocalDateTime
)