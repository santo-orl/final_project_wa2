package it.polito.ticket_catalogue_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tickets")
data class Ticket(
    @Id val ticketId: Long?,
    val ticketType: String,
    val price: Float,
    val minAge: Int?,
    val maxAge: Int?,
    val zid: String,
    val validFrom: String
    )