package it.polito.ticket_catalogue_service.dtos

class CreateTravelcardDTO(
    val cmd: String, //inutile?
    val zones: String,
    val validFrom: String,
    val type: String,
    val username: String,
    val remainingUsages: Int
)