package it.polito.ticket_catalogue_service.repository

import it.polito.ticket_catalogue_service.entities.Ticket
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketCatRepository: CoroutineCrudRepository<Ticket,Long> {


    @Query("""
        SELECT *
        FROM tickets
    """)
    fun getTickets(): Flow<Ticket>


    @Query("SELECT * FROM tickets WHERE ticket_id= :ticketId")
    suspend fun findTicketByTicketId(ticketId: Long): Ticket

}