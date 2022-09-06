package it.polito.login_service.security

import it.polito.login_service.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ServerWebExchange
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationTokenFilter : WebFilter {
    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Value("\${bearerPrefixString}")
    lateinit var bearerPrefixString: String


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if(!shouldApplyFilter(exchange.request)){
            return chain.filter(exchange)
        }
        else {
            val jwt = parseJwt(exchange.request)
            if (jwt != null && jwtUtils.validateJwt(jwt)) { //se è valido il jwt
                var username = ""
                //prendo lo user
                try {
                    username = jwtUtils.getDetailsJwt(jwt).username
                } catch (e: Exception) {
                    //se viene fatta una richiesta e l'utente non è nella tabella user_details_impl
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    return chain.filter(exchange)
                }
                val userDetails: UserDetails = userDetailsServiceImpl.loadUserByUsername(username)
                //lo imposto in authentication in modo da poterlo prendere poi dai controller
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                )
                //vado avanti nella catena di filtri
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
            } else {
                //se il jwt non è valido ritorno 401
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return chain.filter(exchange)
            }
        }

    }

    //prende l'header Authorization, rimuove la sottostringa "Bearer " e ritorna il jwt
    private fun parseJwt(request: ServerHttpRequest): String? {
        val headerAuth = request.headers["Authentication"]?.get(0)
        return if (headerAuth != null && headerAuth.startsWith(bearerPrefixString)) {
            headerAuth.substring(bearerPrefixString.length, headerAuth.length)
        } else null
    }

    //ritorna true solo se il path matcha /admin/**, ovvero l'unico path su cui applicare il filtro
    private fun shouldApplyFilter(request: ServerHttpRequest): Boolean{
        return request.uri.toString().matches(Regex(".+\\/admin\\/.*"))
    }

}