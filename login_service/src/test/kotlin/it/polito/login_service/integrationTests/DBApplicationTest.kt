package it.polito.login_service.integrationTests

import it.polito.login_service.dtos.ActivationDTO
import org.springframework.http.HttpEntity
import it.polito.login_service.dtos.UserDTO
import it.polito.login_service.entities.Activation
import it.polito.login_service.entities.User
import it.polito.login_service.exceptions.ActivationCodeException
import it.polito.login_service.exceptions.ActivationDeadlineException
import it.polito.login_service.exceptions.ActivationIdException
import it.polito.login_service.repositories.UserRepository
import it.polito.login_service.repositories.ActivationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class DBApplicationTest {
    companion object {
        @Container
        private val postgresqlContainer = MyPostgreSQLContainer("postgres:12-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresqlContainer::getUsername)
            registry.add("spring.datasource.password", postgresqlContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 8080

    @Autowired
    lateinit var requester: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Test
    fun emptyParam(){
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("","Giorgia","miaomiao@"))
        val response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun duplicateInUsername(){
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta1.","test@test.it"))
        val response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        val request2 = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta11.","test2@test2.it"))
        val response2 = requester.postForEntity<Unit>("$baseUrl/user/register", request2 )
        assert(response2.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun duplicateInEmail(){
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta1","Bicicletta1.","test@test.it"))
        val response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        val request2 = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta11.","test@test.it"))
        val response2 = requester.postForEntity<Unit>("$baseUrl/user/register", request2 )
        assert(response2.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shortPassword() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("ciaociaociao","Giorgia","miaomiao@"))
        val response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun registrationOk(){
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta123","Bicicletta1.3","test123@test.it"))
        val response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        assert(response.statusCode == HttpStatus.ACCEPTED)
    }

    @Test
    fun rateLimiting(){
        val baseUrl = "http://localhost:$port"
        var request = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta1.","test@test.it"))
        //var response = requester.postForEntity<Unit>("$baseUrl/user/register", request )
        for(i in 1..20)
            requester.postForEntity<Unit>("$baseUrl/user/register", request )
        request = HttpEntity(UserDTO("jimmyCuffiett","Bicicletta11.","teest@test.it"))
        var response = requester.postForEntity<Unit>("$baseUrl/user/register", request)
        println(response.statusCode)
        assert(response.statusCode == HttpStatus.TOO_MANY_REQUESTS)
    }

    @Test
    fun userAdded(){
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta1.","test@test.it"))
        requester.postForEntity<Unit>("$baseUrl/user/register", request )
        var userList = userRepository.findUserByUsername("jimmyCuffietta")
        assert(userList.isNotEmpty())
    }

    @Test
    fun activationAdded() {
        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta1.","test@test.it"))
        requester.postForEntity<Unit>("$baseUrl/user/register", request )
        var activationList = activationRepository.findActivationByUsername("jimmyCuffietta")
        assert(activationList.isNotEmpty())
    }

    /*@Test
    fun validateUserActivationId() {

        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(UserDTO("jimmyCuffietta","Bicicletta1.","test@test.it"))
        requester.postForEntity<Unit>("$baseUrl/user/register", request )
        var activationList = activationRepository.findById(UUID.randomUUID())
        println(activationList)
        assert(activationList.isEmpty)
    }*/

    @Test
    //if activationDeadline is passed, activation record should be removed from table Activation
    fun validateUserActivationDeadlineRemove() {
        var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
        var date : Date = Date(2008, 4, 2, 2, 34, 20) //da mettere nel passato
        var activation =activationRepository.save(Activation(user,"ac",date))

        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(activation.id,activation.activationCode))
        requester.postForEntity<Unit>("$baseUrl/user/validate", request )
        //activation aveva una data nel passato, dovrebbe scomparire dal db in seguito alla richiesta ottenuta
        var activationList = activationRepository.findActivationByUsername("username")
        assert(activationList.isEmpty())
    }

    @Test
    //if activationDeadline is passed, an exception should be thrown
    fun validateUserActivationDeadlineException() {
        //ActivationDeadlineException
        Assertions.assertThrows(ActivationDeadlineException::class.java) {
            var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
            var date : Date = Date() //da mettere nel passato
            var activation =activationRepository.save(Activation(user,"ac",date))

            val baseUrl = "http://localhost:$port"
            val request = HttpEntity(ActivationDTO(activation.id,activation.activationCode))
            requester.postForEntity<Unit>("$baseUrl/user/validate", request )
            //activation aveva una data nel passato, dovrebbe scomparire dal db in seguito alla richiesta ottenuta
            var activationList = activationRepository.findActivationByUsername("username")
        }
    }

    @Test
    //check fun validateUser: if the activation code doesn't match, an exception should be thrown
    fun validateUserActivationCodeException() {
        //ActivationCodeException
        Assertions.assertThrows(ActivationCodeException::class.java) {
            var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
            var date : Date = Date()
            var activation =activationRepository.save(Activation(user,"a",date))

            val baseUrl = "http://localhost:$port"
            val request = HttpEntity(ActivationDTO(activation.id,"b"))
            requester.postForEntity<Unit>("$baseUrl/user/validate", request )
        }
    }

    @Test
    //if attemptCounter is 0, user should be removed from db
    fun validateUserAttemptCounterUserRemoved() {
        var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
        var date : Date = Date()
        var activation =activationRepository.save(Activation(user,"a",date))

        for(i in 1..6)
            HttpEntity(ActivationDTO(activation.id,"b"))

        var userList = userRepository.findUserByUsername("username")
        assert(userList.isEmpty())
    }

    @Test
    //if attemptCounter is 0, activation should be removed from db
    fun validateUserAttemptCounterActivationRemoved() {
        var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
        var date : Date = Date()
        var activation =activationRepository.save(Activation(user,"a",date))

        for(i in 1..6)
            HttpEntity(ActivationDTO(activation.id,"b"))

        var activationList = activationRepository.findActivationByUsername("username")
        assert(activationList.isEmpty())
    }

    @Test
    //if all goes smooth, set to "active" user.status
    fun validateUserActive() {
        var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
        var date : Date = Date()
        var activation =activationRepository.save(Activation(user,"a",date))

        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(activation.id,activation.activationCode))
        requester.postForEntity<Unit>("$baseUrl/user/validate", request )
        user = userRepository.findUserByUsername("username").get(0)

        assert(user.status.equals("active"))

    }

    @Test
    //if all goes smooth, it should return the userDTO
    fun validationCreated() {
        var user = userRepository.save(User("username","passworD1.","email@dfd.dfd","inactive"))
        var date : Date = Date()
        var activation =activationRepository.save(Activation(user,"a",date))

        val baseUrl = "http://localhost:$port"
        val request = HttpEntity(ActivationDTO(activation.id,activation.activationCode))
        val response = requester.postForEntity<Unit>("$baseUrl/user/validate", request )
        assert(response.statusCode == HttpStatus.ACCEPTED)
    }
}