package it.polito.ticket_catalogue_service.repository


import it.polito.ticket_catalogue_service.entities.Travelcard
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TravelcardRepository: CoroutineCrudRepository<Travelcard, Long> {

}