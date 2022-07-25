package it.polito.login_service.repositories

import it.polito.login_service.entities.Activation
import it.polito.login_service.entities.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ActivationRepository: CrudRepository<Activation,UUID> {

    @Query("SELECT a FROM Activation a WHERE a.user.userrname = ?1")
    fun findActivationByUsername(username: String?): List<Activation>

}