package it.polito.ticket_catalogue_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("travelcards")
data class Travelcard(
    @Id val travelcardId: Long?,
    val travelcardType: TravelcardType,
    val price: Float,
    val minAge: Int?,
    val maxAge: Int?,
    val zid: String
)

enum class TravelcardType{
    WEEK,MONTH,YEAR
}