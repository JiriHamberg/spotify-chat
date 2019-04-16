package spotify_chat.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${spotify_chat.frontend.url}")
    private lateinit var frontendHost: String

    override fun configure(http: HttpSecurity?) {
        http?.httpBasic()?.disable()
            ?.csrf()?.disable()
            ?.addFilterBefore(WebSecurityCorsFilter(frontendHost), ChannelProcessingFilter::class.java)
    }
}

class WebSecurityCorsFilter(val corsOrigin: String) : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val res = response as HttpServletResponse
        res.setHeader("Access-Control-Allow-Origin", corsOrigin)
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        res.setHeader("Access-Control-Max-Age", "3600")
        res.setHeader("Access-Control-Allow-Credentials", "true")
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, x-requested-with, Cache-Control, User, Cookie")
        chain?.doFilter(request, res)
    }
}