package it.polito.traveler_service.dtos

import java.time.LocalDateTime

data class TicketValidatedDTO(
    val ticketId: Long,
    val username: String,
    val date: LocalDateTime
)