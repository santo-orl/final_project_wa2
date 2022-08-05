package it.polito.traveler_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TravelerServiceApplication

fun main(args: Array<String>) {
    runApplication<TravelerServiceApplication>(*args)
}
