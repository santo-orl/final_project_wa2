package it.polito.ticket_catalogue_service.dtos

import it.polito.ticket_catalogue_service.entities.TravelcardType

class CreateTravelcardDTO(
    val cmd: String,
    val type: TravelcardType,
    val zones: String,
    val username: String
)