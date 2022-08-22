package it.polito.payment_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("transactions")
data class Transaction(
    @Id
    val transactionId: Long?,
    val orderId: Long,
    val userId : String,
    val orderStatus: String,
    val totalCost: Float,
    val date: LocalDateTime = LocalDateTime.now()
    )