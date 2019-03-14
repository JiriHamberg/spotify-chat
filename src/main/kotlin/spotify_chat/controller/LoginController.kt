package spotify_chat.controller

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import spotify_chat.domain.AccessTokenLifetime
import spotify_chat.domain.AuthorizationCode
import spotify_chat.service.SpotifyService
import spotify_chat.session.SpotifySession
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession


@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"], allowCredentials = "true" )
class LoginController {

    @Autowired
    private lateinit var spotifyService: SpotifyService

    @Autowired
    private lateinit var spotifySession: SpotifySession

    @GetMapping("login")
    fun login(response: HttpServletResponse) {
        val redirectLocation = spotifyService.getAuthorizationUri().toString()

        response.sendRedirect(redirectLocation)
    }


    @GetMapping("logout")
    fun logout(session: HttpSession): String {
        session.invalidate()

        return "Logged out"
    }

    @PostMapping("authenticate")
    fun authenticate(@RequestBody authorization: AuthorizationCode): AccessTokenLifetime {
        val authorizationCode = authorization.authorizationCode
        val credentials: AuthorizationCodeCredentials =  spotifyService.getUserAccessToken(authorizationCode)
        spotifySession.update(credentials)

        return AccessTokenLifetime(spotifySession.getTokenLifeTimeSeconds(), "seconds")
    }


    @GetMapping("login/callback/spotify-auth")
    fun loginLander(@RequestParam(value = "code") authorizationCode: String): String {
        spotifySession.spotifyAuthorizationCode = authorizationCode
        val credentials = spotifyService.getUserAccessToken(authorizationCode)
        spotifySession.update(credentials)

        return "Logged in"
    }

    @GetMapping("accessToken/lifeTime")
    fun getAccessTokenLifetime(): AccessTokenLifetime {
        return AccessTokenLifetime(spotifySession.getTokenLifeTimeSeconds(), "seconds")
    }


    @PostMapping("accessToken/refresh")
    fun refreshAccessToken(): ResponseEntity<*> {
        if(!spotifySession.isSessionActive()) {
            return ResponseEntity("Session has expired", HttpStatus.UNAUTHORIZED)
        }

        val accessToken = spotifySession.spotifyAccessToken.orEmpty()
        val refreshToken = spotifySession.spotifyRefreshToken.orEmpty()

        val credentials = spotifyService.refreshUserAccessToken(accessToken, refreshToken)

        spotifySession.update(credentials)

        return ResponseEntity(
            AccessTokenLifetime(spotifySession.getTokenLifeTimeSeconds(), "seconds"),
            HttpStatus.OK
        )
    }

}