package it.polito.traveler_service.dtos

import it.polito.traveler_service.entities.TicketPurchased
import it.polito.traveler_service.entities.TravelcardPurchased
import java.time.LocalDateTime

data class TravelcardPurchasedDTO (
    val sub: Long,
    var type: TravelcardPurchased.TravelcardType,
    var zid: String,
    var validFrom: LocalDateTime,
    var validTo: LocalDateTime,
    val jws: String
)

fun TravelcardPurchased.toDTO(): TravelcardPurchasedDTO {
    return TravelcardPurchasedDTO(this.sub, this.type, this.zid, this.validFrom,this.validTo,this.toJws())
}