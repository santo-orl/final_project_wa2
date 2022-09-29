package it.polito.traveler_service.entities

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Transit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L
    var date: LocalDate = LocalDate.now()
    @ManyToOne
    @JoinColumn(name = "userDetails")
    var userDetails: UserDetailsImpl? = null

    constructor(date: LocalDate, userDetails: UserDetailsImpl){
        this.date = date
        this.userDetails = userDetails
    }

}