package it.polito.login_service.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.toDTO
import it.polito.login_service.entities.User
import it.polito.login_service.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class JwtUtils {

    @Value("\${jwtValidationKey}")
    lateinit var jwtValidationKey: String

    @Value("\${jwtExpInSecs}")
    lateinit var jwtExpInSecs: String

    @Autowired
    lateinit var userRepository: UserRepository

    fun validateJwt(authToken: String): Boolean {
        //eccezione se firma non valida o exp scaduto
        lateinit var jws: Jws<Claims>
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtValidationKey.toByteArray()).toByteArray())
        try {
            //check signature
            jws = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)

        } catch (e: RuntimeException) {
            println(e.toString())
            return false
        }
        //check datetime
        val iat = jws.body["iat"].toString().toInt() //iat in epoch (secondi)
        val currentEpoch = System.currentTimeMillis() / 1000 //today in seconds from epoch
        if (currentEpoch - iat > jwtExpInSecs.toInt()) {
            return false
        }
        return true
    }


    fun getDetailsJwt(authToken: String): UserDTO {
        //jws sicuramente valido perché è passato da validateJwt
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtValidationKey.toByteArray()).toByteArray())
        var jws = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(authToken)
        val username = jws.body["sub"].toString()
        var u: User
        runBlocking {
            u = userRepository.findUserByUsername(username).first()
        }

        return u.toDTO()
    }

}
