package spotify_chat.controller;


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import spotify_chat.domain.GenericErrorMessage
import spotify_chat.domain.PlayTrack
import spotify_chat.domain.PlaybackMessage
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


    @GetMapping("state")
    fun getCurrentlyPlaying(): ResponseEntity<*> {
        val accessToken =
            spotifySession.spotifyAccessToken ?: return ResponseEntity(GenericErrorMessage("Access token not found"), HttpStatus.UNAUTHORIZED)

        val currentlyPlaying = spotifyService.getCurrentlyPlaying(accessToken)

        return ResponseEntity(currentlyPlaying, HttpStatus.OK)
    }

    @PutMapping("pause")
    fun pausePlayback(request: HttpServletRequest): ResponseEntity<*> {
        val accessToken =
            spotifySession.spotifyAccessToken ?: return ResponseEntity(GenericErrorMessage("Access token not found"), HttpStatus.UNAUTHORIZED)


        spotifyService.pausePlayback(accessToken)

        return ResponseEntity("Playback paused", HttpStatus.OK)
    }

    @PutMapping("play/track")
    fun playTrack(@RequestBody playRequest: PlayTrack): ResponseEntity<PlaybackMessage> {
        val accessToken =
                spotifySession.spotifyAccessToken ?: return ResponseEntity(PlaybackMessage("Unauthorized"), HttpStatus.UNAUTHORIZED)

        val trackUri = playRequest.trackUri

        spotifyService.playTrack(accessToken, trackUri)

        return ResponseEntity(PlaybackMessage("Playing track $trackUri"), HttpStatus.OK)
    }





}
