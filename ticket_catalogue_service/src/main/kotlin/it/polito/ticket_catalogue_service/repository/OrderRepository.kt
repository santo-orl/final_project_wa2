package it.polito.ticket_catalogue_service.repository

import it.polito.ticket_catalogue_service.entities.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.data.r2dbc.repository.Query

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {

    fun findByUserId(userId: String): Flow<Order>
    override suspend fun findById(id:Long):Order

    @Query("""
        UPDATE orders
        SET status = :orderStatus
        WHERE id = :orderId
    """)
    suspend fun updateOrderStatus(orderId:Long, orderStatus:String)

}