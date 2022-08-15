package it.polito.payment_service.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import it.polito.payment_service.entities.Transaction


data class TransactionDTO(

    @JsonProperty("orderId") val orderId: Long?,
    @JsonProperty("orderStatus") val orderStatus: String,
    @JsonProperty("userId") val userId: String,
    @JsonProperty("totalCost") val totalCost: Float
    )

fun Transaction.toDTO(): TransactionDTO {
    return TransactionDTO(orderId,userId,orderStatus,totalCost)
}