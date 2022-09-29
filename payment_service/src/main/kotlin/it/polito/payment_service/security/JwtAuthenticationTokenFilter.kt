package it.polito.payment_service.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationTokenFilter : WebFilter {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Value("\${bearerPrefixString}")
    lateinit var bearerPrefixString: String


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        println("ciao")
        val jwt = parseJwt(exchange.request)
        if (jwt != null && jwtUtils.validateJwt(jwt)) { //se è valido il jwt
            //prendo lo user
            val username: String = jwtUtils.getUsername(jwt)
            //imposto le authorities con il tipo giusto
            println(jwtUtils.getRole(jwt))
            val authorities: MutableSet<GrantedAuthority> = HashSet()
            authorities.add(SimpleGrantedAuthority(jwtUtils.getRole(jwt)))
            //lo imposto in authentication in modo da poterlo prendere poi dai controller
            val authentication = UsernamePasswordAuthenticationToken(
                username, null, authorities)
            //vado avanti nella catena di filtri
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        } else {
            //se il jwt non è valido ritorno 401
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
            return exchange.getResponse().writeWith(Mono.empty());
        }
    }


    //prende l'header Authorization, rimuove la sottostringa "Bearer " e ritorna il jwt
    private fun parseJwt(request: org.springframework.http.server.reactive.ServerHttpRequest): String? {
        val headerAuth = request.headers.get("Authorization")!![0]
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(bearerPrefixString)) {
            headerAuth.substring(bearerPrefixString.length, headerAuth.length)
        } else null
    }




}