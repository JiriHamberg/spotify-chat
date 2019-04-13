package spotify_chat.authentication

import org.springframework.security.core.GrantedAuthority

object AuthorityUser : GrantedAuthority {
    override fun getAuthority(): String = "AUTHORITY_USER"
}