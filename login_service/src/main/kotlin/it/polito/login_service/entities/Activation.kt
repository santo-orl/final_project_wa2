package it.polito.login_service.entities

import java.util.Date
import java.util.UUID
import javax.persistence.*

@Entity
class Activation: EntityBase<UUID> {

    //random id used during registration
    @Id
    var id: UUID = UUID.randomUUID()

    @OneToOne
    var user: User = User()

    var activationCode: String = ""
    @Temporal(TemporalType.TIMESTAMP)
    var activationDeadline: Date = Date()
    var attemptCounter: Int = 5

    constructor()
    constructor(id: UUID, user: User, activationCode: String, activationDeadline: String, attemptCounter: Int)
    constructor(user: User, activationCode: String, activationDeadline: Date){
        this.user=user
        this.activationCode=activationCode
        this.activationDeadline=activationDeadline
    }

}