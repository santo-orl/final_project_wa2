package it.polito.traveler_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import it.polito.traveler_service.entities.TravelcardPurchased

data class CreateTravelcardDTO(

    @JsonProperty("cmd")
    val cmd: String, //inutile?

    @JsonProperty("type")
    val type: TravelcardPurchased.TravelcardType,

    @JsonProperty("zones")
    val zones: String,

    @JsonProperty("username")
    val username: String
)