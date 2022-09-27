package it.polito.traveler_service.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Transit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L
    var date: LocalDateTime = LocalDateTime.now()
    @ManyToOne(cascade=[CascadeType.ALL])
    @JoinColumn(name = "userDetails")
    var userDetails: UserDetailsImpl? = null

    constructor(date: LocalDateTime, userDetails: UserDetailsImpl){
        this.date = date
        this.userDetails = userDetails
    }

}