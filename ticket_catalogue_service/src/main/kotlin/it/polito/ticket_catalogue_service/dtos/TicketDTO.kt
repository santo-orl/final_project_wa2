package it.polito.ticket_catalogue_service.dtos

import it.polito.ticket_catalogue_service.entities.Ticket

class TicketDTO (
    val ticketType: String,
    val price: Float,
    val minAge: Int?,
    val maxAge: Int?,
    val zid: String,
    val validFrom: String
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(this.ticketType, this.price, this.minAge, this.maxAge,this.zid,this.validFrom)
}
