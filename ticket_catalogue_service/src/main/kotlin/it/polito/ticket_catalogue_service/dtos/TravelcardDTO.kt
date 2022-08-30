package it.polito.ticket_catalogue_service.dtos

import it.polito.ticket_catalogue_service.entities.Travelcard
import it.polito.ticket_catalogue_service.entities.TravelcardType

data class TravelcardDTO (
    val travelcardType: TravelcardType,
    val price: Float,
    val minAge: Int?,
    val maxAge: Int?,
    val zid: String
    )

fun Travelcard.toDTO(): TravelcardDTO {
    return TravelcardDTO(this.travelcardType, this.price, this.minAge, this.maxAge,this.zid)
}