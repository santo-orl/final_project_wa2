package it.polito.login_service.security

import it.polito.login_service.entities.User
import it.polito.login_service.repositories.UserRepository
import it.polito.login_service.services.UserDetailsServiceImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationTokenFilter : WebFilter {
    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${bearerPrefixString}")
    lateinit var bearerPrefixString: String


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!shouldApplyFilter(exchange.request)) {
            return chain.filter(exchange)
        } else {
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
                var user: User
                runBlocking {
                    user = userRepository.findUserByUsername(username).first()
                }
                val authorities: MutableSet<GrantedAuthority> = HashSet()
                authorities.add(SimpleGrantedAuthority(user.roleToString()))
                //lo imposto in authentication in modo da poterlo prendere poi dai controller
                val authentication = UsernamePasswordAuthenticationToken(
                    username, null, authorities
                )
                //vado avanti nella catena di filtri
                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
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
    private fun shouldApplyFilter(request: ServerHttpRequest): Boolean {
        return request.uri.toString().matches(Regex(".+\\/admin\\/.*"))
    }

}