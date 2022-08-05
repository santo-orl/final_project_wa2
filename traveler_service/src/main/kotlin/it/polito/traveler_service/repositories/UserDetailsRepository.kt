package it.polito.traveler_service.repositories

import it.polito.traveler_service.entities.UserDetailsImpl
import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository: CrudRepository<UserDetailsImpl,Long> {

    @Query("SELECT u FROM UserDetailsImpl u WHERE u.userr = ?1")
    fun findUserDetailsByUserr(username: String?): List<UserDetailsImpl>

    @Query("SELECT DISTINCT u.userr FROM UserDetailsImpl u")
    fun findAllTravelers(): List<String>
}