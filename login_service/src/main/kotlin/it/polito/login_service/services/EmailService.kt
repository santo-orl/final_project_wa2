package it.polito.login_service.services

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage


@Service
class EmailService {

    data class Email(
        val to: String,
        val subject: String,
        val text: String,
        val withAttachment: Boolean
    )


    var emailSender : JavaMailSender = JavaMailSenderImpl()

    fun sendMail(email: Email) {
        val msg = createSimpleMessage(email)
        emailSender.send(msg)
    }

    private fun createSimpleMessage(email: Email): MimeMessage {
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)

        setupMessage(helper, email)

        return message
    }


    private fun setupMessage(helper: MimeMessageHelper, email: Email) {
        helper.setTo(email.to)
        helper.setSubject(email.subject)
        helper.setText(email.text)
    }

}