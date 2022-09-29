package it.polito.login_service.rateLimiting

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class RateLimiter {

    val bucket : Bucket

     init {
        var refill: Refill = Refill.intervally(10, Duration.ofSeconds(1))
        var limit: Bandwidth = Bandwidth.classic(10, refill)
        bucket = Bucket4j.builder()
                .addLimit(limit)
                .build()
    }
}