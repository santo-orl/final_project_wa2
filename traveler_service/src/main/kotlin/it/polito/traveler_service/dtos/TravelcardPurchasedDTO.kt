package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.entities.TravelcardPurchased
import java.time.LocalDateTime

data class TravelcardPurchasedDTO (
    val sub: Long,
    val iat: LocalDateTime,
    val exp: LocalDateTime,
    val zid: String,
    val validFrom: LocalDateTime,
    val type: String,
    val remainingUsages: Int,
    val jws: String
)

fun TravelcardPurchased.toDTO(): TravelcardPurchasedDTO {
    return TravelcardPurchasedDTO(this.sub, this.iat, this.exp, this.zid,this.validFrom,this.type,this.remainingUsages,this.toJws())
}