package it.polito.traveler_service.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.Column

data class TicketValidatedDTO(
    val ticketId: Long,
    val username: String,
    val date: String
)