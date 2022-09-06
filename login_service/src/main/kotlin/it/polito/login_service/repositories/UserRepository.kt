package it.polito.login_service.repositories

import it.polito.login_service.entities.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CoroutineCrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userrname = ?1")
    fun findUserByUsername(username: String?): Flow<User>

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    fun findUserByEmail(email: String?): Flow<User>
}