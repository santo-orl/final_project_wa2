package it.polito.login_service.dtos

import it.polito.login_service.entities.Activation
import java.util.*

data class ActivationDTO(
    val id: UUID,
    val activationCode: String
)

fun Activation.toDTO(): ActivationDTO{
    return ActivationDTO(id,activationCode)
}