package spotify_chat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spotify_chat.service.SpotifyService
import spotify_chat.session.SpotifySession


@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"], allowCredentials = "true")
@RequestMapping("user")
class UserController {

    @Autowired
    private lateinit var spotifySession: SpotifySession

    @Autowired
    private lateinit var spotifyService: SpotifyService


    @GetMapping("profile")
    fun getProfile(): ResponseEntity<*> {
        val accessToken = spotifySession.spotifyAccessToken

        return if(accessToken == null) {
            ResponseEntity("Session has expired", HttpStatus.UNAUTHORIZED)
        } else {
            val userProfile = spotifyService.getProfile(accessToken)
            ResponseEntity(userProfile, HttpStatus.OK)
        }
    }

}