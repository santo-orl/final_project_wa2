package it.polito.ticket_catalogue_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionDTO (
    @JsonProperty("orderId") val orderId: Long?,
    @JsonProperty("orderStatus") val orderStatus: String,
    @JsonProperty("userId") val userId: String,
    @JsonProperty("totalCost") val totalCost: Float
)