package it.polito.login_service.rateLimiting


import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Component
class RateLimiterConfig(val rateLimiter: RateLimiter): WebMvcConfigurer {

    private  var interceptor = RateLimiterInterceptor(rateLimiter)

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(interceptor).addPathPatterns("/user/**")
    }

}