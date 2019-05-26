package spotify_chat.interceptor

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessagingException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import spotify_chat.domain.chat.ChatSubscriptionEvent
import java.security.Principal
import java.util.concurrent.ConcurrentHashMap


@Component
class ChatChannelInterceptor : ChannelInterceptor, ApplicationEventPublisherAware {

    private lateinit var publisher: ApplicationEventPublisher

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val chatMessagesChannelRe = "/topic/([a-zA-Z1-9]+)/messages".toRegex()

    // principalName -> (subscription -> trackId) mapping
    private val chatSubscriptions: ConcurrentHashMap<String, ConcurrentHashMap<String, String>> = ConcurrentHashMap()


    override fun setApplicationEventPublisher(applicationEventPublisher: ApplicationEventPublisher) {
        publisher = applicationEventPublisher
    }


    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val headerAccessor = StompHeaderAccessor.wrap(message)
        val principal = headerAccessor.user ?: throw MessagingException("User is unauthorized")
        val destination = headerAccessor.destination
        val subscriptionId = headerAccessor.subscriptionId

        //logger.info(chatSubscriptions.toString())

        when(headerAccessor.command) {
            StompCommand.SUBSCRIBE -> handleSubscribe(principal, destination, subscriptionId)
            StompCommand.UNSUBSCRIBE -> handleUnsubscribe(principal, subscriptionId)
            StompCommand.DISCONNECT -> handleDisconnect(principal)
            else -> Unit
        }

        return message
    }


    private fun handleSubscribe(principal: Principal, destination: String, subscriptionId: String) {
        chatMessagesChannelRe.matchEntire(destination)?.let { match ->
            val (trackId) = match.destructured

            chatSubscriptions.compute(principal.name) { key: String, value: ConcurrentHashMap<String, String>? ->
                if(value == null) {
                    val subs = ConcurrentHashMap<String, String>()
                    subs[subscriptionId] = trackId
                    subs
                } else {
                    value[subscriptionId] = trackId
                    value
                }
            }

            val subscriptionEvent = ChatSubscriptionEvent.Subscribe(this, principal, trackId)
            publisher.publishEvent(subscriptionEvent)
        }
    }


    private fun handleUnsubscribe(principal: Principal, subscriptionId: String) {
        chatSubscriptions[principal.name]?.get(subscriptionId)?.let { trackId ->
            val unsubscriptionEvent = ChatSubscriptionEvent.Unsubscribe(this, principal, trackId)

            chatSubscriptions[principal.name]?.remove(subscriptionId)
            publisher.publishEvent(unsubscriptionEvent)
        }
    }


    private fun handleDisconnect(principal: Principal) {
        val disconnectEvent = ChatSubscriptionEvent.Disconnect(this, principal)

        chatSubscriptions.remove(principal.name)
        publisher.publishEvent(disconnectEvent)
    }

}