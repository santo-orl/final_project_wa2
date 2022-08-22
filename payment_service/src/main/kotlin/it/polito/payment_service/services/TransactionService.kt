package it.polito.payment_service.services

import it.polito.payment_service.dtos.TransactionDTO
import it.polito.payment_service.dtos.toDTO
import it.polito.payment_service.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import it.polito.payment_service.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

@Service
class TransactionService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    fun findAllTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAll()
            .map { transaction -> transaction.toDTO() }
    }

    fun findUserTransactions(userId: String): Flow<TransactionDTO> {
        var ret = transactionRepository.findByUserId(userId).map { transaction -> transaction.toDTO() }
        if (ret == null) throw Exception("Transactions not found")
        return ret
    }

    fun getUserTransactionsInTimeRange(userId: String, from: LocalDateTime, to: LocalDateTime): Flow<TransactionDTO> {
        if (transactionRepository.findByUserId(userId) == null) throw UserNotFoundException("user not found")
        return transactionRepository.findByUserId(userId)
            .filter { transaction -> transaction.date.isAfter(from) && transaction.date.isBefore(to) }
            .map { transaction -> transaction.toDTO() }
    }

    fun getTransactionsInTimeRange(from: LocalDateTime, to: LocalDateTime): Flow<TransactionDTO> {
        return transactionRepository.findAll()
            .filter { transaction -> transaction.date.isAfter(from) && transaction.date.isBefore(to) }
            .map { transaction -> transaction.toDTO() }
    }

}