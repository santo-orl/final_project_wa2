package it.polito.payment_service.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtils {

    @Value("\${jwtValidationKey}")
    lateinit var jwtValidationKey: String

    @Value("\${jwtExpInSecs}")
    lateinit var jwtExpInSecs: String

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

    fun getUsername(authToken: String): String {
        //jws sicuramente valido perché è passato da validateJwt
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtValidationKey.toByteArray()).toByteArray())
        var jws = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(authToken)
        println("")
        return jws.body["sub"].toString()
    }

    fun getRole(authToken: String): String {
        //jws sicuramente valido perché è passato da validateJwt
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtValidationKey.toByteArray()).toByteArray())
        var jws = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(authToken)
        return jws.body["role"].toString()
    }

}