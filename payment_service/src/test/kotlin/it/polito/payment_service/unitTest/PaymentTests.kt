package it.polito.payment_service.unitTest

import it.polito.payment_service.repositories.TransactionRepository
import it.polito.payment_service.services.TransactionService
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.junit4.SpringRunner

class PaymentTests {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class PaymentTest{

        lateinit var transactionService: TransactionService
        lateinit var transactionRepository: TransactionRepository

        init {
            transactionService = Mockito.mock(TransactionService::class.java)
            transactionRepository = Mockito.mock(TransactionRepository::class.java)
        }

        @Test
        //supponendo non sia vuoto
        suspend fun checkGetAllTransactions(){
            var ret = transactionService.findAllTransactions()
            assert(ret.toList().isNotEmpty())
        }

        @Test
        //caso in cui ret == null
        fun checkFindUserTransaction(){
            Assertions.assertThrows(Exception::class.java){
                var ret = transactionService.findUserTransactions("userIDExample")
            }
        }
    }
}