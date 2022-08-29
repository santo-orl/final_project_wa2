package it.polito.traveler_service.dtos

data class CreateTravelcardDTO(
    val cmd: String, //inutile?
    val zones: String,
    val validFrom: String,
    val type: String,
    val username: String,
    val remainingUsages: Int
)