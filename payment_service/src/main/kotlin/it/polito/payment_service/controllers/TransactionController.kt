package it.polito.payment_service.controllers

import it.polito.payment_service.dtos.TransactionDTO
import it.polito.payment_service.services.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import kotlinx.coroutines.flow.Flow
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
class TransactionController {

    @Autowired
    lateinit var transactionService: TransactionService

     @GetMapping("/admin/transactions", produces = [MediaType.APPLICATION_NDJSON_VALUE])
     fun getAllTransactions(@RequestHeader("authorization") jwt: String): ResponseEntity<Flow<TransactionDTO>>{
         val transactions = transactionService.findAllTransactions()
         return ResponseEntity.ok(transactions)
     }

    @GetMapping("/transactions", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getUserTransactions(@RequestHeader("authorization") jwt: String, princ:Principal): ResponseEntity<Flow<TransactionDTO>>{
        val transactions = transactionService.findUserTransactions(princ.name)
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/admin/transactions/{user}/range", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getUserTransactionsInTimeRange(@RequestHeader("authorization") jwt: String,@PathVariable("user") user: String, princ:Principal, @RequestParam("from") from: String,@RequestParam("to") to: String): ResponseEntity<Flow<TransactionDTO>>{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val transactions = transactionService.getUserTransactionsInTimeRange(user, LocalDate.parse(from,formatter),
            LocalDate.parse(to,formatter))
        return ResponseEntity.ok(transactions)
    }

    @GetMapping("/admin/transactions/range", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getTransactionsInRange(@RequestHeader("authorization") jwt: String, @RequestParam("from") from: String,@RequestParam("to") to: String): ResponseEntity<Flow<TransactionDTO>>{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val transactions = transactionService.getTransactionsInTimeRange(LocalDate.parse(from,formatter),LocalDate.parse(to,formatter))
        return ResponseEntity.ok(transactions)
    }

}