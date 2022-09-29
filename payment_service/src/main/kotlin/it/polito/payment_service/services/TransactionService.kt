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
import java.time.LocalDate
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
        return transactionRepository.findByUserId(userId).map { transaction -> transaction.toDTO() }
    }

    fun getUserTransactionsInTimeRange(userId: String, from: LocalDate, to: LocalDate): Flow<TransactionDTO> {
        return transactionRepository.findByUserId(userId)
            .filter { transaction -> transaction.getDateAsLocalDate().isAfter(from) && transaction.getDateAsLocalDate().isBefore(to) }
            .map { transaction -> transaction.toDTO() }
    }

    fun getTransactionsInTimeRange(from: LocalDate, to: LocalDate): Flow<TransactionDTO> {
        return transactionRepository.findAll()
            .filter { transaction -> transaction.getDateAsLocalDate().isAfter(from) && transaction.getDateAsLocalDate().isBefore(to) }
            .map { transaction -> transaction.toDTO() }
    }

}