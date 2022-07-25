package it.polito.login_service.rateLimiting


import io.github.bucket4j.*
import it.polito.login_service.controllers.UserController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class RateLimiterInterceptor(val rateLimiter: RateLimiter) : HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean{
        var probe: ConsumptionProbe = rateLimiter.bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", probe.getRemainingTokens().toString())
            return true
        } else {
            var waitForRefill = probe.getNanosToWaitForRefill()
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),"You have exhausted your API Request Quota")
            println("Too many requests")
            return false;
        }
    }
}