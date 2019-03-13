package spotify_chat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import spotify_chat.service.SpotifyService

@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"])
@RequestMapping("album")
class AlbumController {


    @Autowired
    lateinit var spotifyService: SpotifyService

    @GetMapping("{albumId}")
    fun greeting(@PathVariable(value = "albumId") albumId: String) = spotifyService.getAlbum(albumId)

}