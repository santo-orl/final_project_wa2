package it.polito.login_service.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users") //"users" and not "user" because the last one is a private keyword in Postgres
data class User(
    @Id var id: Long = 0L,
    var role: Role = Role.CUSTOMER,
    var userrname: String = "",
    var passsword: String = "",
    var email: String = "",
    var status: String = "" //active or inactive
) {
    constructor(username: String, password: String, email: String, status: String)
            : this(0, Role.CUSTOMER, username, password, email, status)

    constructor(username: String, password: String, email: String, status: String, role: Role)
            : this(0, role, username, password, email, status)

    fun roleToString(): String {
        if (role == Role.CUSTOMER) return "CUSTOMER"
        else if (role == Role.QR_READER) return "QR_READER"
        else return "ADMIN"
    }

}

enum class Role {
    CUSTOMER, ADMIN, QR_READER
}