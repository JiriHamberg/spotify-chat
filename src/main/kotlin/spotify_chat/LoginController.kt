package spotify_chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import spotify_chat.domain.AccessTokenLifetime
import spotify_chat.service.SpotifyService
import spotify_chat.session.SpotifySession
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession


@RestController
class LoginController {

    @Autowired
    private lateinit var spotifyService: SpotifyService

    @Autowired
    private lateinit var spotifySession: SpotifySession

    @RequestMapping("login")
    fun login(response: HttpServletResponse) {
        val redirectLocation = spotifyService.getAuthorizationUri().toString()

        response.sendRedirect(redirectLocation)
    }


    @RequestMapping("logout")
    fun logout(session: HttpSession): String {
        session.invalidate()

        return "Logged out"
    }


    @RequestMapping("login/callback/spotify-auth")
    fun loginLander(@RequestParam(value = "code") authorizationCode: String): String {
        spotifySession.spotifyAuthorizationCode = authorizationCode
        val credentials = spotifyService.getUserAccessToken(authorizationCode)
        spotifySession.update(credentials)

        return "Logged in"
    }

    @RequestMapping("accessToken/lifeTime")
    fun getAccessTokenLifetime(): AccessTokenLifetime {
        return AccessTokenLifetime(spotifySession.getTokenLifeTimeSeconds(), "seconds")
    }


    @RequestMapping("accessToken/refresh")
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
            HttpStatus.UNAUTHORIZED)
    }

}