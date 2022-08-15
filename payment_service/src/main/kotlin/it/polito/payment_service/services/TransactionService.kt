package it.polito.payment_service.services

import it.polito.payment_service.dtos.TransactionDTO
import it.polito.payment_service.dtos.toDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import it.polito.payment_service.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Service
class TransactionService {

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    fun findAllTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAll()
            .map { transaction -> transaction.toDTO() }
    }

    fun findUserTransactions(userId: String): Flow<TransactionDTO> {
        var ret = transactionRepository.findByUserId(userId).map{transaction -> transaction.toDTO()}
        if(ret==null) throw Exception("Transactions not found")
        return ret
    }
}