package it.polito.traveler_service.entities

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class UserDetailsImpl : UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L
    var userr = "" //username
    var name: String = ""
    var address: String = ""
    var dateOfBirth: String = ""
    var telephoneNumber: String = ""
    var role: String = ""

    @OneToMany(mappedBy = "userDetails",cascade=[CascadeType.ALL])
    var ticketList: List<TicketPurchased>? = null
    @OneToMany(mappedBy = "userDetails",cascade=[CascadeType.ALL])
    var travelcardList: List<TravelcardPurchased>? = null
    @OneToMany(mappedBy = "userDetails",cascade=[CascadeType.ALL])
    var transitList: List<Transit>? = null

    //specifico quali siano le authorities dell'userr
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities: MutableSet<GrantedAuthority> = HashSet()
        authorities.add(SimpleGrantedAuthority(role))
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return userr
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }


}