package it.polito.ticket_catalogue_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ShopRequestDTO (
        @JsonProperty("nTickets")
        val nTickets: Int,
        @JsonProperty("ticketId")
        val ticketId: Long,
        @JsonProperty("creditCardNumber")
        val creditCardNumber: Int,
        @JsonProperty("expDate")
        val expDate: String,
        @JsonProperty("cvv")
        val cvv: Int,
        @JsonProperty("cardHolder")
        val cardHolder: String
    )