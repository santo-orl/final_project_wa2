package it.polito.ticket_catalogue_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ShopTravelcardRequestDTO(
    @JsonProperty("travelcardId")
    val travelcardId: Long,
    @JsonProperty("creditCardNumber")
    val creditCardNumber: Int,
    @JsonProperty("expDate")
    val expDate: String,
    @JsonProperty("cvv")
    val cvv: Int,
    @JsonProperty("cardHolder")
    val cardHolder: String
)