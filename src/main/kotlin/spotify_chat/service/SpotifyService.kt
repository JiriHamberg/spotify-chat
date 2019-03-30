package spotify_chat.service

import com.google.gson.JsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying
import com.wrapper.spotify.model_objects.specification.Album
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import com.wrapper.spotify.model_objects.specification.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest
import org.springframework.beans.factory.annotation.Autowired
import spotify_chat.session.SpotifySession
import java.time.LocalDateTime
import java.util.*


@Component
class SpotifyService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${spotify_chat.client.id}")
    private lateinit var clientId: String

    @Value("\${spotify_chat.client.secret}")
    private lateinit var clientSecret: String

    @Value("#{new java.net.URI('\${spotify_chat.client.redirectUri}')}")
    private lateinit var clientRedirectUri: URI

    private lateinit var spotifyApi: SpotifyApi


    @PostConstruct
    private fun postConstruct() {
        spotifyApi = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(clientRedirectUri)
            .build()

        val clientCredentialsRequest = spotifyApi.clientCredentials()
            .build()

        val clientCredentials = clientCredentialsRequest.execute()
        spotifyApi.accessToken = clientCredentials.accessToken

    }


    fun findTracks(name: String, trackCount: Int): Paging<Track> {
        val request = spotifyApi.searchTracks(name)
            .limit(trackCount)
            .build()

        return request.execute()
    }


    fun getAlbum(id: String): Album {
        val request = spotifyApi.getAlbum(id).build()

        return request.execute()
    }


    fun getAuthorizationUri(): URI {
        val authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
            .scope("user-read-playback-state,user-modify-playback-state,,user-read-email")
            .show_dialog(true)
            .build()

        return authorizationCodeUriRequest.execute()
    }


    fun getProfile(accessToken: String): User {
        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .build()

        return client.currentUsersProfile.build().execute()
    }

    fun getUserAccessToken(code: String): AuthorizationCodeCredentials {
        val userAccessTokenRequest = spotifyApi.authorizationCode(code)
            .grant_type("authorization_code")
            .build()

        return userAccessTokenRequest.execute()
    }


    fun refreshUserAccessToken(accessToken: String, refreshToken: String): AuthorizationCodeCredentials {

        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .setRefreshToken(refreshToken)
            .build()

        return client.authorizationCodeRefresh().build().execute()
    }


    fun pausePlayback(accessToken: String) {
        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .build()

        client.pauseUsersPlayback()
            .build()
            .execute()
    }


    fun resumePlayback(accessToken: String) {
        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .build()

        val setPlaybackRequest = client.startResumeUsersPlayback()
            .build()

        setPlaybackRequest.execute()
    }


    fun playTrack(accessToken: String, trackUri: String) {
        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .build()

        val trackUris = JsonArray()
        trackUris.add(trackUri)

        val setPlaybackRequest = client.startResumeUsersPlayback()
            .uris(trackUris)
            .build()

        setPlaybackRequest.execute()
    }


    fun getCurrentlyPlaying(accessToken: String): CurrentlyPlaying {
        val client = SpotifyApi.builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setAccessToken(accessToken)
            .build()

        return client.usersCurrentlyPlayingTrack
            .build()
            .execute()
    }

}