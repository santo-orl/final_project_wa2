package it.polito.payment_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentOutcomeDTO (
    @JsonProperty("orderId")
    val orderId: Long,
    @JsonProperty("outcome")
    val outcome: Boolean,
    @JsonProperty("jwt")
    val jwt:String
)