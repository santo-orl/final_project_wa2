package it.polito.login_service.unitTests

import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.entities.Role
import it.polito.login_service.exceptions.BadCredentialsException
import it.polito.login_service.repositories.ActivationRepository
import it.polito.login_service.repositories.UserRepository
import it.polito.login_service.services.EmailService
import it.polito.login_service.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import it.polito.login_service.entities.User
import org.junit.Before


class UserServiceUnitTest {

    /******************************** Testing fun registerUser *********************************************/



    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class RegisterUserTest(/*val bCryptPasswordEncoder: BCryptPasswordEncoder*/) {

        lateinit var activationRepo: ActivationRepository

        //@MockBean
        lateinit var userRepo: UserRepository

        lateinit var emailService: EmailService
        lateinit var userService: UserService

        @Bean
        fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

        @Bean
        fun mailSender(): JavaMailSender {
            val mailSender = JavaMailSenderImpl()
            mailSender.host = "smtp.libero.it"
            mailSender.port = 587
            mailSender.username = "finalprojectwa2@libero.it"
            mailSender.password = "Wa2polito123."
            val javaMailProperties = mailSender.javaMailProperties
            javaMailProperties["mail.smtp.auth"] = true
            javaMailProperties["mail.smtp.starttls.enable"] = true
            javaMailProperties["mail.transport.protocol"] = "smtp"
            mailSender.javaMailProperties = javaMailProperties
            return mailSender
        }

        init {
            fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
            activationRepo = Mockito.mock(ActivationRepository::class.java)
            userRepo = Mockito.mock(UserRepository::class.java)
            emailService = EmailService()
            userService = UserService(activationRepo, userRepo, mailSender(), bCryptPasswordEncoder())
        }

        @Test
        //check fun registerUser: username should not be empty
        fun registerUserEmptyUsername() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("", "GiorgiaChiotti1.", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should not be empty
        fun registerUserEmptyPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: email should not be empty
        fun registerUserEmptyEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "GiorgiaChiotti1.", ""), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password shouldn't have empty spaces
        fun registerUserPasswordWithWhitespace() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1. bau", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should be at least 8 characters long
        fun registerUserShortPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "miao", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 digit
        fun registerUserPasswordWithoutDigits() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti.", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 lowercase letter
        fun registerUserPasswordWithoutLowercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GIORGIACHIOTTI1.", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 uppercase letter
        fun registerUserPasswordWithoutUppercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "giorgiachiotti1.", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 non alphanumeric character
        fun registerUserPasswordWithoutAlphanumeric() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1", "miao@miao.it"), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: email should have a correct format (ex a@a.a)
        fun registerUserInvalidEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "1GiorgiaChiotti1.", "miao@miao."), Role.CUSTOMER)
            }
        }

        @Test
        //check fun registerUser: a user having the given parameters should be added to the Users table
        fun userAdded() {
            //user should be in db
            userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "caramella@albicocca.it"), Role.CUSTOMER)
            var userList = userRepo.findAll()//userRepo.findUserByUsername("Giovanni")
            println(userList.toString())
            var tmp = listOf<User>()
            Mockito.`when`(userRepo.findUserByUsername("Giovanni")).thenReturn(tmp)
            //Assertions.assertNotNull(userList)
        }

        /*******************************************************************************************************/
    }
}