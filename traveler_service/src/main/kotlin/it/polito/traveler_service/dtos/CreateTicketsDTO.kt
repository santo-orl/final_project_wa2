package it.polito.traveler_service.dtos

data class CreateTicketsDTO(
    val cmd: String,
    val quantity: Int,
    val zones: String,
    val validFrom: String,
    val type: String
)