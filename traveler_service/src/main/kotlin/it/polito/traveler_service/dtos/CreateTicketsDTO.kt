package it.polito.traveler_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateTicketsDTO(

    @JsonProperty("cmd")
    val cmd: String, //inutile?

    @JsonProperty("quantity")
    val quantity: Int,

    @JsonProperty("zones")
    val zones: String,

    @JsonProperty("validFrom")
    val validFrom: String,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("username")
    val username: String
)