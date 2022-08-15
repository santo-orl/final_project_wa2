package it.polito.payment_service.repositories

import it.polito.payment_service.entities.Transaction
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository: CoroutineCrudRepository<Transaction, Long> {

    fun findByUserId(userId: String): Flow<Transaction>
}