package it.polito.login_service.controllers

import it.polito.login_service.services.EmailService
import kotlinx.coroutines.FlowPreview
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController {

    @Autowired
    lateinit var emailSender: JavaMailSender

    //funzione creata se si fa una post a questo indirizzo
    //usata solo per vedere se il blocco "invio mail" funzionava
    @FlowPreview
    @PostMapping("/mail/send")
    fun sendEmail(@RequestBody mail: EmailService.Email): Unit {
        val message = SimpleMailMessage()
        message.setFrom("finalprojectwa2@libero.it")
        message.setTo("email@prova.it")
        message.setSubject("prova")
        message.setText("aaaa")
        emailSender.send(message)
    }

}