package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.TravelcardPurchased

data class CreateTravelcardDTO(
    val cmd: String, //inutile?
    val type: TravelcardPurchased.TravelcardType,
    val zones: String,
    val username: String
)