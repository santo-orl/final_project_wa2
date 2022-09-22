package it.polito.login_service.unitTests

import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.entities.Role
import it.polito.login_service.entities.User
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit4.SpringRunner
import java.util.stream.DoubleStream.builder
import javax.annotation.Resource
import javax.transaction.Transactional
class AdminServiceUnitTest {

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class RegisterAdminTest(){

        lateinit var activationRepo: ActivationRepository

        @Autowired
        lateinit var userRepo: UserRepository
        lateinit var emailService: EmailService
        lateinit var userService: UserService

        @Bean
        fun emailSender(): JavaMailSender {
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
        @Bean
        fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
        init {
            activationRepo = Mockito.mock(ActivationRepository::class.java)
            userRepo = Mockito.mock(UserRepository::class.java)
            emailService = EmailService()
            userService = UserService(activationRepo, userRepo, emailSender(), bCryptPasswordEncoder())
        }

        @Test
        //check fun registerUser: username should not be empty
        fun registerAdminEmptyUsername() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should not be empty
        fun registerAdminEmptyPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: email should not be empty
        fun registerAdminEmptyEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "GiorgiaChiotti1.", ""), Role.ADMIN)
            }
        }


        @Test
        //check fun registerUser: password shouldn't have empty spaces
        fun registerAdminPasswordWithWhitespace() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1. bau", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should be at least 8 characters long
        fun registerAdminShortPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "miao", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 digit
        fun registerAdminPasswordWithoutDigits() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti.", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 lowercase letter
        fun registerAdminPasswordWithoutLowercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GIORGIACHIOTTI1.", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 uppercase letter
        fun registerAdminPasswordWithoutUppercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "giorgiachiotti1.", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 non alphanumeric character
        fun registerAdminPasswordWithoutAlphanumeric() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1", "miao@miao.it"), Role.ADMIN)
            }
        }

        @Test
        //check fun registerUser: email should have a correct format (ex a@a.a)
        fun registerAdminInvalidEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "1GiorgiaChiotti1.", "miao@miao."), Role.ADMIN)
            }
        }




        /**********************************/

        @Test
        //check fun registerUser: email should be unique
        fun registerAdminDuplicateEmail() {
            //BadCredentialsException
            //userService.registerUser(UserDTO("Antonio", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
            userRepo.save(User(0,"miao","Miao123","cacca@cacca.it","INACTIVE"))
            var t = userRepo.findUserByUsername("miao")
            println(t)
        /*  Assertions.assertThrows(BadCredentialsException::class.java) {
                       userRepo.save(User(1, "bau", "Miao123", "cacca@cacca.it", "INACTIVE"))
                   }
                       Assertions.assertThrows(BadCredentialsException::class.java) {
                           userService.registerUser(UserDTO("Antonio", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
                           userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
                       }*/
        }

        @Test
        //check fun registerUser: an activation having the given parameters should be added to the Activation table
        fun activationAdded() {
            //activation should be in db
            userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
            var activationList = activationRepo.findActivationByUsername("Giovanni")
            assert(activationList.isNotEmpty())
        }

        @Test
        //check fun registerUser: a user having the given parameters should be added to the Users table
        fun adminAdded() {
            //user should be in db
            userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
            var userList = userRepo.findUserByUsername("Giovanni")
            assert(userList.isNotEmpty())
        }

        @Test
        //check fun registerUser: username should be unique
        fun registerAdminDuplicateUsername() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "Password1.", "mo@miao.it"), Role.ADMIN)
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"), Role.ADMIN)
            }

        }
    }
}