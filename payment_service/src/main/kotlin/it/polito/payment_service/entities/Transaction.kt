package it.polito.payment_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("transactions")
data class Transaction(
    @Id
    val transactionId: Long?,
    val orderId: Long,
    val userId : String,
    val orderStatus: String,
    val totalCost: Float
    )