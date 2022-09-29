package it.polito.login_service

import it.polito.login_service.scheduledJobs.SchedulerConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class LoginServiceApplication {

    fun schedule() {
        SpringApplication.run(SchedulerConfig::class.java)
    }

    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.office365.com"
        mailSender.port = 587
        mailSender.username = "orlando_santo@outlook.it"
        mailSender.password = "or21lan10do98."
        val javaMailProperties = mailSender.javaMailProperties
        javaMailProperties["mail.smtp.port"] = 587
        javaMailProperties["mail.smtp.auth"] = true
        javaMailProperties["mail.smtp.starttls.enable"] = true
        javaMailProperties["mail.smtp.ssl.enable"] = true
        javaMailProperties["mail.transport.protocol"] = "smtp"
        mailSender.javaMailProperties = javaMailProperties
        return mailSender
    }
}

fun main(args: Array<String>) {
    runApplication<LoginServiceApplication>(*args)
}
