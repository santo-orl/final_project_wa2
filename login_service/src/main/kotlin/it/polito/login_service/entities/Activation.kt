package it.polito.login_service.entities

import java.util.Date
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("activation")
data class Activation(
    @Id var id: UUID = UUID.randomUUID(),
    var user: User, //TODO mettere il tipo o la chiave esterna (Long)?
    var activationCode: String,
    var activationDeadline: Date,
    var attemptCounter: Int = 5
) {
    constructor(): this(UUID.randomUUID(),User(),"",Date(),5)
    constructor(user: User, activationCode: String, activationDeadline: Date)
        : this(UUID.randomUUID(),user,activationCode, activationDeadline,5)
}




