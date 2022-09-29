package it.polito.payment_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Table("transactions")
data class Transaction(
    @Id
    val transactionId: Long?,
    val orderId: Long,
    val userId : String,
    val orderStatus: String,
    val totalCost: Float,
    val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
    ){

    fun getDateAsLocalDate(): LocalDate{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val ret = LocalDate.parse(date, formatter)
        return ret
    }

}