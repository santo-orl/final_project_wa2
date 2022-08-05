package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.TicketPurchased
import java.time.LocalDateTime

data class TicketPurchasedDTO(
    val sub: Long,
    val iat: LocalDateTime,
    val exp: LocalDateTime,
    val zid: String,
    val validFrom: LocalDateTime,
    val type: String,
    val jws: String
)
fun TicketPurchased.toDTO(): TicketPurchasedDTO {
    return TicketPurchasedDTO(this.sub, this.iat, this.exp, this.zid,this.validFrom,this.type, this.toJws())
}