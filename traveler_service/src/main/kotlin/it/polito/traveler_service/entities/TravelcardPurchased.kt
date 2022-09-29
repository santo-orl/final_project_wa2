package it.polito.traveler_service.entities

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.*

@Entity
class TravelcardPurchased {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var sub: Long = 0L
    var type: TravelcardType
    var zid: String
    var validFrom: LocalDateTime = LocalDateTime.now()
    var validTo: LocalDateTime = LocalDateTime.now()
    @ManyToOne
    @JoinColumn(name = "userDetails")
    var userDetails: UserDetailsImpl? = null


    constructor(type: TravelcardType, zid: String, validFrom: LocalDateTime, validTo: LocalDateTime, userDetails: UserDetailsImpl){
        this.type=type
        this.zid=zid
        this.validFrom=validFrom
        this.validTo=validTo
        this.userDetails=userDetails
    }

    fun toJws(): String {
        val accessKey = "30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiE"
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(accessKey.toByteArray()).toByteArray())
        val jwt = Jwts.builder()
            .setSubject(sub.toString())
            .claim("zid", zid)
            .claim("validFrom", validFrom.toString())
            .claim("validTo", validTo.toString())
            .claim("type", type)
            .signWith(key)
            .compact()
        return jwt.toString()
    }

    enum class TravelcardType{
        WEEK,MONTH,YEAR
    }

}