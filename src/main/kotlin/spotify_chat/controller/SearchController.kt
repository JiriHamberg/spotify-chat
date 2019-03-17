package spotify_chat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import spotify_chat.service.SpotifyService


@RestController
@CrossOrigin(origins = ["\${spotify_chat.frontend.url}"], allowCredentials = "true")
@RequestMapping("search")
class SearchController {

    @Autowired
    lateinit var spotifyService: SpotifyService

    @GetMapping("track")
    fun searchTracks(@RequestParam(value = "name", required = true) name: String,
                     @RequestParam(value = "count", required = false, defaultValue = "25") count: Int) =
        spotifyService.findTracks(name, count)


}