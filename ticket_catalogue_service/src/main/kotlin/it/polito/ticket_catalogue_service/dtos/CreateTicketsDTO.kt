package it.polito.ticket_catalogue_service.dtos

data class CreateTicketsDTO(
    val cmd: String, //inutile?
    val quantity: Int,
    val zones: String,
    val validFrom: String,
    val type: String,
    val username: String
)