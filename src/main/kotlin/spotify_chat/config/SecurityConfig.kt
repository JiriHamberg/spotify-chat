package spotify_chat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
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
        http?.csrf()?.disable()
        http?.addFilterBefore(WebSecurityCorsFilter(frontendHost), ChannelProcessingFilter::class.java)
    }
}

class WebSecurityCorsFilter(val corsOrigin: String) : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val res = response as HttpServletResponse
        res.setHeader("Access-Control-Allow-Origin", corsOrigin)
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        res.setHeader("Access-Control-Max-Age", "3600")
        res.setHeader("Access-Control-Allow-Credentials", "true")
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, x-requested-with, Cache-Control")
        chain?.doFilter(request, res)
    }
}
