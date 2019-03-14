package spotify_chat.controller;


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spotify_chat.service.SpotifyService
import spotify_chat.session.SpotifySession
import javax.servlet.http.HttpServletRequest

@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"], allowCredentials = "true")
@RequestMapping("playback")
class PlaybackController {

    @Autowired
    private lateinit var spotifyService: SpotifyService

    @Autowired
    private lateinit var spotifySession: SpotifySession

    private val logger = LoggerFactory.getLogger(PlaybackController::class.java)


    @PutMapping("pause")
    fun pausePlayback(request: HttpServletRequest): ResponseEntity<*> {
        val accessToken =
            spotifySession.spotifyAccessToken ?: return ResponseEntity("Access token not found", HttpStatus.UNAUTHORIZED)


        spotifyService.pausePlayback(accessToken)

        return ResponseEntity("Playback paused", HttpStatus.OK)
    }

}
