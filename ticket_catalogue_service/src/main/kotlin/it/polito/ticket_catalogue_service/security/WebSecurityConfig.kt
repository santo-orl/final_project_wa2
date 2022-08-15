package it.polito.ticket_catalogue_service.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig {

    @Autowired
    lateinit var filter: JwtAuthenticationTokenFilter

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http
            .csrf().disable()
            .addFilterAt(filter,SecurityWebFiltersOrder.AUTHORIZATION)
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/tickets").permitAll()
            .pathMatchers(HttpMethod.POST,"/admin/tickets").hasAuthority("ADMIN")
            .pathMatchers(HttpMethod.POST,"/shop").hasAnyAuthority("CUSTOMER","ADMIN")
            .anyExchange().permitAll()
            .and().build()
    }

}