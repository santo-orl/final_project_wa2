package it.polito.login_service.entities

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users") //"users" and not "user" because the last one is a private keyword in Postgres
class User: EntityBase<Long>,UserDetails{//class

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L

    var role: Role = Role.CUSTOMER
    var userrname: String = ""
    var passsword: String = ""
    var email: String = ""
    var status: String = "" //active or inactive

    constructor()
    constructor(id: Long, username: String, password: String, email:String, status: String)
    constructor(username: String, password: String, email:String, status: String){
        this.userrname=username
        this.passsword=password
        this.email=email
        this.status=status
    }

    constructor(username: String, password: String, email:String, status: String,role:Role){
        this.userrname=username
        this.passsword=password
        this.email=email
        this.status=status
        this.role=role
    }

    override fun toString(): String{
        return "USER username: $username, password: $password, email: $email, status: $status, id: $id"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities: MutableSet<GrantedAuthority> = HashSet()
        authorities.add(SimpleGrantedAuthority(role.toString()))
        return authorities
    }

    override fun getPassword(): String {
        return passsword
    }

    override fun getUsername(): String {
        return userrname
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

    fun roleToString():String{
        if(role == Role.CUSTOMER){ return "CUSTOMER" }
        else{ return "ADMIN" }
    }

}

//enum class
enum class Role{
    CUSTOMER, ADMIN
}