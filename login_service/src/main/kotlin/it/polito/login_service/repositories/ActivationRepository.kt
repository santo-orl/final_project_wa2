package it.polito.login_service.repositories

import it.polito.login_service.entities.Activation
import it.polito.login_service.entities.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ActivationRepository: CoroutineCrudRepository<Activation, UUID> {

    @Query("SELECT a FROM Activation a WHERE a.user.userrname = ?1")
    fun findActivationByUsername(username: String?): Flow<Activation>

}