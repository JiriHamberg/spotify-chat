package spotify_chat.authentication

import com.wrapper.spotify.model_objects.specification.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class SpotifyAuthentication(val principal: String, val accessToken: String) : Authentication {

    private var isAuthenticated: Boolean = true


    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }

    override fun getName(): String = principal


    override fun getCredentials(): Any = accessToken


    override fun getPrincipal(): Any = principal


    override fun isAuthenticated(): Boolean = isAuthenticated


    override fun getDetails(): Any = Unit


    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(AuthorityUser)
    }

}