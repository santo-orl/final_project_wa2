package it.polito.login_service.entities

import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users") //"users" and not "user" because the last one is a private keyword in Postgres
class User: EntityBase<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L

    var role: Role = Role.CUSTOMER
    var username: String = ""
    var password: String = ""
    var email: String = ""
    var status: String = "" //active or inactive

    constructor()
    constructor(id: Long, username: String, password: String, email:String, status: String)
    constructor(username: String, password: String, email:String, status: String){
        this.username=username
        this.password=password
        this.email=email
        this.status=status
    }

    override fun toString(): String{
        return "USER username: $username, password: $password, email: $email, status: $status, id: $id"
    }

    fun roleToString():String{
        if(role == Role.CUSTOMER){ return "CUSTOMER" }
        else{ return "ADMIN" }
    }

}//class

//enum class
enum class Role{
    CUSTOMER, ADMIN
}