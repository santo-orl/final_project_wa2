package it.polito.traveler_service.security

import it.polito.traveler_service.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtAuthenticationTokenFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    @Value("\${bearerPrefixString}")
    lateinit var bearerPrefixString: String


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val jwt = parseJwt(request)
        if (jwt != null && jwtUtils.validateJwt(jwt)) { //se è valido il jwt
            var username = ""
            //prendo lo user
            try{
                username = jwtUtils.getDetailsJwt(jwt).userr
            }catch(e:Exception){
                //se viene fatta una richiesta e l'utente non è nella tabella user_details_impl
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: User not registered");
                return
            }
            val userDetails: UserDetails = userDetailsServiceImpl.loadUserByUsername(username)
            //lo imposto in authentication in modo da poterlo prendere poi dai controller
            val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities())
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
            //vado avanti nella catena di filtri
            filterChain.doFilter(request, response)
        } else {
            //se il jwt non è valido ritorno 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        }


    }

    //prende l'header Authorization, rimuove la sottostringa "Bearer " e ritorna il jwt
    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(bearerPrefixString)) {
            headerAuth.substring(bearerPrefixString.length, headerAuth.length)
        } else null
    }


}