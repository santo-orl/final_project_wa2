package it.polito.login_service.controllers

import it.polito.login_service.dtos.ActivationDTO
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.UserLoginDTO
import it.polito.login_service.entities.Role
import it.polito.login_service.exceptions.*
import it.polito.login_service.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/hello")
    fun hello() = "hello"


    @PostMapping("/user/register", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun registration(@RequestBody user: UserDTO): ResponseEntity<String> {
        var id: UUID
        try {
            id = userService.registerUser(user,Role.CUSTOMER)
        } catch (e: BadCredentialsException) {
            //username o password non validi
            println("Exception: $e, credentials not valid")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val email = user.email
        return ResponseEntity("{\"provisional_id\":\"$id\",\"email\":\"$email\"}", HttpStatus.ACCEPTED)
    }


    @PostMapping("/user/validate", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun validation(@RequestBody activation: ActivationDTO): ResponseEntity<String> {
        var user: UserDTO
        try {
            user = userService.validateUser(activation)
        } catch (e: ActivationDeadlineException) {
            //scaduto il periodo per l'attivazione
            println("Exception: $e, expired deadline")
            return ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: ActivationIdException) {
            //id non valido
            println("Exception: $e, invalid id")
            return ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: ActivationCodeException) {
            //code non valido
            println("Exception: $e, invalid code")
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        val userId = user.id
        val username = user.username
        val email = user.email

        return ResponseEntity("{\"userId\":\"$userId\",\"username\":\"$username\",\"email\":\"$email\"}", HttpStatus.CREATED)
    }


    @PostMapping("/user/login", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun logUser(@RequestBody requester: UserLoginDTO): ResponseEntity<String> {
        val username = requester.username
        val password = requester.password
        var role: Role
        //if login succeded
        try {
            //return del role dell'user
            role = userService.logUser(username, password)
        } catch (e: BadLoginException) {
            println("Exception: $e, bad login")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }catch(e: IndexOutOfBoundsException){
            println("Exception: $e, invalid credentials")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val ret = userService.toJWT(username, Date(), role)
        //qua ritorno il jwt come responseEntity con sub,iat,exp,role
        return ResponseEntity<String>(ret, HttpStatus.OK)
    }

    @PostMapping("/admin/register", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun registrationAdmin(@RequestBody user: UserDTO): ResponseEntity<String> {
        var id: UUID
        try {
            id = userService.registerUser(user,Role.ADMIN)
        } catch (e: BadCredentialsException) {
            //username o password non validi
            println("Exception: $e, credentials not valid")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val email = user.email
        return ResponseEntity("{\"provisional_id\":\"$id\",\"email\":\"$email\"}", HttpStatus.ACCEPTED)
    }


}