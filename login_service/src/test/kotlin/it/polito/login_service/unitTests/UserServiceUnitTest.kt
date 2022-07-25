package it.polito.login_service.unitTests

import it.polito.login_service.dtos.UserDTO
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit4.SpringRunner


class UserServiceUnitTest {

    /******************************** Testing fun registerUser *********************************************/

    @RunWith(SpringRunner::class)
    @ExtendWith(MockitoExtension::class)
    class RegisterUserTest(val bCryptPasswordEncoder: BCryptPasswordEncoder) {

        lateinit var activationRepo: ActivationRepository
        lateinit var userRepo: UserRepository
        lateinit var emailService: EmailService
        lateinit var userService: UserService

        init {
            activationRepo = Mockito.mock(ActivationRepository::class.java)
            userRepo = Mockito.mock(UserRepository::class.java)
            emailService = EmailService()
            userService = UserService(activationRepo, userRepo, emailService, bCryptPasswordEncoder)
        }

        @Test
        //check fun registerUser: username should not be empty
        fun registerUserEmptyUsername() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("", "GiorgiaChiotti1.", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should not be empty
        fun registerUserEmptyPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: email should not be empty
        fun registerUserEmptyEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("miao", "GiorgiaChiotti1.", ""))
            }
        }

        @Test
        //check fun registerUser: username should be unique
        fun registerUserDuplicateUsername() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "Password1.", "mo@miao.it"))
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"))
            }

        }

        @Test
        //check fun registerUser: email should be unique
        fun registerUserDuplicateEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Antonio", "GiorgiaChiotti1.", "miao@miao.it"))
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password shouldn't have empty spaces
        fun registerUserPasswordWithWhitespace() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1. bau", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should be at least 8 characters long
        fun registerUserShortPassword() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "miao", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 digit
        fun registerUserPasswordWithoutDigits() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti.", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 lowercase letter
        fun registerUserPasswordWithoutLowercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GIORGIACHIOTTI1.", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 uppercase letter
        fun registerUserPasswordWithoutUppercase() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "giorgiachiotti1.", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: password should have at least 1 non alphanumeric character
        fun registerUserPasswordWithoutAlphanumeric() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1", "miao@miao.it"))
            }
        }

        @Test
        //check fun registerUser: email should have a correct format (ex a@a.a)
        fun registerUserInvalidEmail() {
            //BadCredentialsException
            Assertions.assertThrows(BadCredentialsException::class.java) {
                userService.registerUser(UserDTO("Giovanni", "1GiorgiaChiotti1.", "miao@miao."))
            }
        }

        @Test
        //check fun registerUser: a user having the given parameters should be added to the Users table
        fun userAdded() {
            //user should be in db
            userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"))
            var userList = userRepo.findUserByUsername("Giovanni")
            assert(userList.isNotEmpty())
        }

        @Test
        //check fun registerUser: an activation having the given parameters should be added to the Activation table
        fun activationAdded() {
            //activation should be in db
            userService.registerUser(UserDTO("Giovanni", "GiorgiaChiotti1.", "miao@miao.it"))
            var activationList = activationRepo.findActivationByUsername("Giovanni")
            assert(activationList.isNotEmpty())
        }
    }


    /******************************** Testing fun validateUser *********************************************/

    //TODO
    /*
    @RunWith(SpringRunner::class)
    class ValidateUserTest {
        lateinit var activationRepo: ActivationRepository
        lateinit var userRepo: UserRepository
        lateinit var emailService: EmailService
        lateinit var userService: UserService
        init {
            activationRepo = Mockito.mock(ActivationRepository::class.java)
            userRepo = Mockito.mock(UserRepository::class.java)
            emailService = EmailService()
            userService = UserService(activationRepo, userRepo, emailService)
        }
        @Test
        //check fun validateUser: activation id should exist in table Activation
        fun validateUserActivationId() {
            //ActivationCodeException
            Assertions.assertThrows(ActivationCodeException::class.java) {
                var activationId = UUID.randomUUID()
                var activationCode = "a"
                userService.validateUser(activationId, activationCode)
            }
            //per far sollevare questa eccezione devo verificare che non esista una riga
            //che io sto cercando in questo test
        }
        @Test
        //check fun validateUser: if activationDeadline is passed, activation record should be removed from table Activation
        fun validateUserActivationDeadlineRemove() {
            //activation should not be in db
        }
        @Test
        //check fun validateUser: if activationDeadline is passed, an exception should be thrown
        fun validateUserActivationDeadlineException() {
            //ActivationDeadlineException
            Assertions.assertThrows(ActivationIdException::class.java) {
                lateinit var activationRepo: ActivationRepository
                lateinit var userRepo: UserRepository
                lateinit var emailService: EmailService
                lateinit var activationId: UUID
                lateinit var activationCode: String
                val userService = UserService(activationRepo, userRepo, emailService)
                userService.validateUser(activationId, activationCode)
                //per far sollevare questa eccezione devo far in modo che il rigo nel db abbia una data scaduta
            }
        }
        @Test
        //check fun validateUser: if the activation code doesn't match, an exception should be thrown
        fun validateUserActivationCodeException() {
            //ActivationCodeException
            Assertions.assertThrows(ActivationCodeException::class.java) {
                lateinit var activationRepo: ActivationRepository
                lateinit var userRepo: UserRepository
                lateinit var emailService: EmailService
                lateinit var activationId: UUID
                lateinit var activationCode: String
                val userService = UserService(activationRepo, userRepo, emailService)
                userService.validateUser(activationId, activationCode)
                //per far sollevare questa eccezione devo far in modo che
                //l'activation code sia sbagliato
            }
        }
        @Test
        //check fun validateUser: if attemptCounter is 0, user should be removed from db
        fun validateUserAttemptCounterUserRemoved() {
            //user should not be in db
        }
        @Test
        //check fun validateUser: if attemptCounter is 0, activation should be removed from db
        fun validateUserAttemptCounterActivationRemoved() {
            //activation should not be in db
        }
        @Test
        //check fun validateUser: if all goes smooth, set to "active" user.status
        fun validateUserActive() {
            //check in db if user.status is active
            lateinit var activationRepo: ActivationRepository
            lateinit var userRepo: UserRepository
            lateinit var emailService: EmailService
            lateinit var activationId: UUID
            lateinit var activationCode: String
            val userService = UserService(activationRepo, userRepo, emailService)
            userService.validateUser(activationId, activationCode)
            //assert( user.status == "active")
        }
        @Test
        //check fun validateUser: if all goes smooth, it should return the userDTO
        fun validateUserToDTO() {
            lateinit var activationRepo: ActivationRepository
            lateinit var userRepo: UserRepository
            lateinit var emailService: EmailService
            lateinit var activationId: UUID
            lateinit var activationCode: String
            val userService = UserService(activationRepo, userRepo, emailService)
            Assertions.assertInstanceOf(UserDTO::class.java, userService.validateUser(activationId, activationCode))
        }
    }
     */
}