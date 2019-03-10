package spotify_chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import spotify_chat.service.SpotifyService


@RestController
@RequestMapping("search")
class SearchController {

    @Autowired
    lateinit var spotifyService: SpotifyService

    @GetMapping("track")
    fun greeting(@RequestParam(value = "name", required = true) name: String) = spotifyService.findTracks(name)


}