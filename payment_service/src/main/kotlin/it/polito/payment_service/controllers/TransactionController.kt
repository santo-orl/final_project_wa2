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
import java.security.Principal

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
}