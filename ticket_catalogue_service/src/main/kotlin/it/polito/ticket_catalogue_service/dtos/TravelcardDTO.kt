package it.polito.ticket_catalogue_service.dtos

import it.polito.ticket_catalogue_service.entities.Travelcard

data class TravelcardDTO (
    val travelcardType: String,
    val price: Float,
    val minAge: Int?,
    val maxAge: Int?,
    val zid: String,
    val validFrom: String,
    val maxUsages: Int
    )

fun Travelcard.toDTO(): TravelcardDTO {
    return TravelcardDTO(this.travelcardType, this.price, this.minAge, this.maxAge,this.zid,this.validFrom,this.maxUsages)
}