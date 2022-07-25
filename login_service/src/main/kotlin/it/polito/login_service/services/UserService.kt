package it.polito.login_service.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import it.polito.login_service.dtos.ActivationDTO
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.dtos.toDTO
import it.polito.login_service.entities.Activation
import it.polito.login_service.entities.Role
import it.polito.login_service.entities.User
import it.polito.login_service.exceptions.*
import it.polito.login_service.repositories.ActivationRepository
import it.polito.login_service.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern
import javax.transaction.Transactional


@Service
@Transactional
class UserService(var activationRepository: ActivationRepository,
                  var userRepository: UserRepository,
                  var emailService: EmailService,
                  val bCryptPasswordEncoder: BCryptPasswordEncoder) {


    @Autowired
    lateinit var emailSender: JavaMailSender

    //used in /user/register controller
    fun registerUser(userDTO: UserDTO, role: Role): UUID {
        val username = userDTO.username
        val password = userDTO.password
        val email = userDTO.email

        //validate parameters
        if (username === "" || password === "" || email === "") {
            throw BadCredentialsException("Empty parameters")
        }
        if (userRepository.findUserByUsername(username).isNotEmpty()) {
            throw BadCredentialsException("Username already exists")
        }
        if (userRepository.findUserByEmail(email).isNotEmpty()) {
            throw BadCredentialsException("Email already used")
        }
        if ((password.length < 8) ||
                (password.contains(" ")) ||
                (!Pattern.compile(".*\\d.*").matcher(password).matches()) ||
                (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) ||
                (!Pattern.compile(".*[a-z].*").matcher(password).matches()) ||
                (!Pattern.compile(".*[^a-zA-Z\\d].*").matcher(password).matches())) {  //contains special characters
            throw BadCredentialsException("Password not strong enough")
        }
        if (!Pattern.compile(".+@.+\\..+").matcher(email).matches()) {
            throw BadCredentialsException("Invalid email address")
        }

        //hashing della psw
        var pswHash = bCryptPasswordEncoder.encode(password)

        //create user and save into db with hashed psw
        var user: User = User(username, pswHash/*password*/, email, "inactive",role)
        user = userRepository.save(user)

        //create activation and save into db
        val activationCode: String = Math.random().toString()
        val c: Calendar = Calendar.getInstance()
        c.add(Calendar.DATE, 7) //activation date will be 7 days from today
        val d: Date = c.time
        var activation: Activation = Activation(user, activationCode, d)
        activation = activationRepository.save(activation)

        //send email containing the activation code
        val message = SimpleMailMessage()
        message.setFrom("donotreplyfinalproject@gmail.com")
        message.setTo(user.email)
        message.setSubject("RECEIVING ACTIVATION CODE")
        message.setText("activation code: "+activationCode)
        emailSender.send(message)

        //return activation id
        return activation.id
    }


    //used in /user/validate controller
    fun validateUser(activationDTO: ActivationDTO): UserDTO {
        var activationID = activationDTO.id
        var activationCode = activationDTO.activationCode
        var activation: Activation = Activation()

        //trovo activation corrispondente al randomID (potrebbe non esistere)
        try {
            activation = activationRepository.findById(activationID).get()

        } catch (e: IllegalArgumentException) { //if activationID does not exist
            throw ActivationIdException("Invalid activation ID")
        }
        //se è passata la data di scadenza, rimuovo il record da Activation e ritorno 404 dal controller
        if (Calendar.getInstance().time.after(activation.activationDeadline)) {
            activationRepository.delete(activation)
            throw ActivationDeadlineException("Activation deadline expired")
        }
        //se l'activation code non corrisponde, nel controller ritorno 404 e qui decremento attemptCounter
        if (!activationCode.equals(activation.activationCode)) {
            activation.attemptCounter--
            //se attemptCounter è 0 rimuovo il record in Activation e in User
            if (activation.attemptCounter === 0) {
                userRepository.delete(activation.user)
                activationRepository.delete(activation)
            }
            throw ActivationCodeException("Wrong activation code")
        }

        //setto a "active" lo status dello user corrispondente a questa activation (chiave esterna)
        var user: User = activation.user
        user.status = "active"
        activationRepository.delete(activation)

        return user.toDTO()

    }

    //login
    fun logUser(username:String,password:String):Role{
        val user = userRepository.findUserByUsername(username).get(0)
        if(!bCryptPasswordEncoder.matches(password,user.password)){
            throw BadLoginException("Bad login :( wrong user or psw :'( ")
        }
        /*if(password != user.password){
            throw BadLoginException("Bad login :( wrong user or psw :'( ")
        }*/
        return user.role
    }

    //create jwt from params
    fun toJWT(username:String, date:Date,role: Role):String{
        val accessKey = "30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiEauzNN7V0aB30poSilNi15HCiE"
        val key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(accessKey.toByteArray()).toByteArray())
        var roleString = ""
        if(role==Role.CUSTOMER) roleString = "CUSTOMER"
        else roleString = "ADMIN"
        val jwt = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(date)
            .setExpiration(Date(date.time + 1000 * 60 * 60))//60min * 60sec *1000ms
            .claim("role",roleString)
            .signWith(key)
            .compact()
        return jwt
    }

    @Scheduled(fixedRate =60000)
    //function for periodic check on expired registration data each minute
    fun checkExpRegData() {
        println("Checking on expired registration data . . . ")
        var activationList = activationRepository.findAll()
        for (activation in activationList)
            if (Calendar.getInstance().time.after(activation.activationDeadline)) {
                println("Expired registration data found . . . ")
                activationRepository.delete(activation)
            }
    }

}