package it.polito.ticket_catalogue_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("orders")
data class Order (
        @Id val id: Long?,
        val userId: String,     // username
        val status: String, //PENDING o COMPLETED o CANCELED
        val nTickets: Int,
        val ticketId: Long, //"ticketId" ma va bene anche per travelcardId
        val type: OrderType
)

enum class OrderType{TICKET,TRAVELCARD}