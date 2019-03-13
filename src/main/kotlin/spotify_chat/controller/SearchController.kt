package spotify_chat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import spotify_chat.service.SpotifyService


@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"])
@RequestMapping("search")
class SearchController {

    @Autowired
    lateinit var spotifyService: SpotifyService

    @GetMapping("track")
    fun greeting(@RequestParam(value = "name", required = true) name: String) = spotifyService.findTracks(name)


}