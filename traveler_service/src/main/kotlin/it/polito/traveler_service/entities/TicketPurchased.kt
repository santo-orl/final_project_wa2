package it.polito.traveler_service.entities

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.*

@Entity
class TicketPurchased {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var sub: Long = 0L
    var iat: LocalDateTime = LocalDateTime.now()
    var exp: LocalDateTime = LocalDateTime.now()
    var zid: String = ""
    var validFrom: LocalDateTime = LocalDateTime.now()
    var type: String = ""
    @ManyToOne
    @JoinColumn(name = "userDetails")
    var userDetails: UserDetailsImpl? = null

    constructor(iat: LocalDateTime, zid: String, userDetailsImpl: UserDetailsImpl, validFrom: LocalDateTime, type: String) {
        this.iat = iat
        this.exp = iat.plusHours(1)
        this.zid = zid
        this.userDetails = userDetailsImpl
        this.validFrom = validFrom
        this.type = type
    }

    fun toJws(): String {
        val iatDate = Date.from(iat.atZone(ZoneId.systemDefault()).toInstant())
        val expDate = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant())
        val accessKey = "30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiE"
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(accessKey.toByteArray()).toByteArray())
        val jwt = Jwts.builder()
                .setSubject(sub.toString())
                .setIssuedAt(iatDate)
                .setExpiration(expDate)
                .claim("zid", zid)
                .claim("validFrom", validFrom.toString())
                .claim("type", type)
                .signWith(key)
                .compact()
        return jwt.toString()
    }

}