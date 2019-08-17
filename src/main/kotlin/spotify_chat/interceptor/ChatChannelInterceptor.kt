package spotify_chat.interceptor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
import org.springframework.messaging.support.MessageBuilder
import org.springframework.util.MultiValueMap


@Component
class ChatChannelInterceptor : ChannelInterceptor, ApplicationEventPublisherAware {

    @Value("\${spotify_chat.messaging.stomp.relay.username}")
    private lateinit var relayUsername: String

    @Value("\${spotify_chat.messaging.stomp.relay.password}")
    private lateinit var relayPassword: String


    private lateinit var publisher: ApplicationEventPublisher

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val chatMessagesChannelRe = "/topic/([a-zA-Z1-9]+)\\.messages".toRegex()

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

        if(headerAccessor.command == StompCommand.STOMP) {
            return handleConnect(message, StompCommand.STOMP)
        } else if (headerAccessor.command == StompCommand.CONNECT) {
            return handleConnect(message, StompCommand.CONNECT)
        }

        when(headerAccessor.command) {
            StompCommand.SUBSCRIBE -> handleSubscribe(principal, destination, subscriptionId)
            StompCommand.UNSUBSCRIBE -> handleUnsubscribe(principal, subscriptionId)
            StompCommand.DISCONNECT -> handleDisconnect(principal)
            else -> Unit
        }

        return message
    }


    /**
     * Override STOMP login and passcode to use server credentials - authentication is done using spring session
     */
    private fun handleConnect(message: Message<*>, command: StompCommand): Message<*> {
        val headerAccessor = StompHeaderAccessor.wrap(message)

        val accessor = StompHeaderAccessor.create(command)
        accessor.setAcceptVersion(headerAccessor.version)
        accessor.sessionId = headerAccessor.sessionId
        accessor.login = relayUsername
        accessor.passcode = relayPassword

        logger.info("Message intercepted, setting login to ${accessor.login}")

        return MessageBuilder.createMessage(ByteArray(0), accessor.messageHeaders)
    }


    private fun handleSubscribe(principal: Principal, destination: String, subscriptionId: String) {

        logger.info("handleSubscribe: principal=$principal destination=$destination")

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

            logger.info(chatSubscriptions.toString())

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