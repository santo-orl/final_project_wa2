package it.polito.traveler_service.security

import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class WebSecurityConfig(val bCryptPasswordEncoder: BCryptPasswordEncoder, userDetailsServiceImpl: UserDetailsServiceImpl) : WebSecurityConfigurerAdapter() {

    @Bean
    fun authenticationJwtTokenFilter(): JwtAuthenticationTokenFilter? {
        return JwtAuthenticationTokenFilter()
    }


    //qui gestisco la protezione degli url
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/my/**").hasAnyAuthority("CUSTOMER","ADMIN")
                .antMatchers("/qr/**").hasAuthority("QR_READER")
    }

}