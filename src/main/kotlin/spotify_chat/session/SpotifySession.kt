package spotify_chat.session

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope
import java.io.Serializable
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class SpotifySession : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    var spotifyAuthorizationCode: String? = null
    var spotifyAccessToken: String? = null
    var spotifyRefreshToken: String? = null
    var expirationTime: LocalDateTime? = null


    fun getTokenLifeTimeSeconds() =
        if(expirationTime == null)
            -1
        else
            LocalDateTime.now().until(expirationTime, ChronoUnit.SECONDS)

    fun isAuthorized(): Boolean = spotifyAuthorizationCode != null

    fun isSessionActive(): Boolean = expirationTime != null && LocalDateTime.now().isBefore(expirationTime)

    fun update(credentials: AuthorizationCodeCredentials) {
        spotifyAccessToken = credentials.accessToken
        if (credentials.refreshToken != null) {
            spotifyRefreshToken = credentials.refreshToken
        }
        expirationTime = LocalDateTime.now().plusSeconds(credentials.expiresIn.toLong())
    }
}