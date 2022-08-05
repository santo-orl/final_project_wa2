package it.polito.login_service.security


import it.polito.login_service.entities.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfiguration(val bCryptPasswordEncoder: BCryptPasswordEncoder) : WebSecurityConfigurerAdapter() {
    @Bean
    fun authenticationJwtTokenFilter(): JwtAuthenticationTokenFilter? {
        return JwtAuthenticationTokenFilter()
    }

    //qui gestisco la protezione degli url
    override fun configure(http: HttpSecurity) {
        //super.configure(http)
        http
            .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers("/admin").hasRole(Role.ADMIN.toString())
                .antMatchers("/user").permitAll()
                .and()
                .csrf().disable()
    }


}