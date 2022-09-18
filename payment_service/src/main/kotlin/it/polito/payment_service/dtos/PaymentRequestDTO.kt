package it.polito.payment_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentRequestDTO (
    @JsonProperty("cardHolder")
    val cardHolder: String,
    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,
    @JsonProperty("expDate")
    val expDate: String,
    @JsonProperty("cvv")
    val cvv: String,
    @JsonProperty("orderId")
    val orderId: Long,
    @JsonProperty("totalCost")
    val totalCost: Float,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("jwt")
    val jwt: String
)