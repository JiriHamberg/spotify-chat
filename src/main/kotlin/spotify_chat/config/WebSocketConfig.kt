package spotify_chat.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import spotify_chat.interceptor.ChatChannelInterceptor


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    @Value("\${spotify_chat.frontend.url}")
    private lateinit var frontEndUrl: String

    @Value("\${spotify_chat.messaging.stomp.relay.host}")
    private lateinit var relayHost: String

    @Value("\${spotify_chat.messaging.stomp.relay.port}")
    private lateinit var relayPort: Integer

    @Value("\${spotify_chat.messaging.stomp.relay.username}")
    private lateinit var relayUsername: String

    @Value("\${spotify_chat.messaging.stomp.relay.password}")
    private lateinit var relayPassword: String

    @Value("\${spotify_chat.messaging.stomp.relay.virtualHost}")
    private lateinit var relayVirtualHost: String

    @Autowired
    private lateinit var chatChannelInterceptor: ChatChannelInterceptor


    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.setApplicationDestinationPrefixes("/app")

        val stompBrokerRegistration = config.enableStompBrokerRelay("/topic")
            .setRelayHost(relayHost)
            .setRelayPort(relayPort.toInt())
            .setClientLogin(relayUsername)
            .setClientPasscode(relayPassword)

        if(relayVirtualHost.isNotEmpty()) {
            stompBrokerRegistration.setVirtualHost(relayVirtualHost)
        }
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat")
            .setAllowedOrigins(frontEndUrl)
            .addInterceptors(HttpSessionHandshakeInterceptor())
            .withSockJS()
    }


    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        super.configureClientInboundChannel(registration)
        registration.interceptors(chatChannelInterceptor)
    }

}



