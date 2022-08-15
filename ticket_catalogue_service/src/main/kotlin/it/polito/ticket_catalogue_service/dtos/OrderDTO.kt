package it.polito.ticket_catalogue_service.dtos

import it.polito.ticket_catalogue_service.entities.Order


data class OrderDTO(
        val userId: String,
        val status: String
)

fun Order.toDTO(): OrderDTO{
    return OrderDTO(this.userId,this.status)
}