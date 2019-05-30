package spotify_chat.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import spotify_chat.domain.chat.*
import java.util.concurrent.ConcurrentHashMap


@Component
class ChatSubscriberService: ApplicationListener<ChatSubscriptionEvent> {

    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate

    private val subscriptionsByUser: ConcurrentHashMap<String, MutableSet<String>> = ConcurrentHashMap()
    private val subscribersByTrackId: ConcurrentHashMap<String, MutableSet<String>> = ConcurrentHashMap()

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)


    override fun onApplicationEvent(event: ChatSubscriptionEvent) {
        when(event) {
            is ChatSubscriptionEvent.Subscribe -> {
                logger.debug("on subscribe: $event")
                addSubscription(event.principal.name, event.trackId)
                publishSubscriptionEvent(event)
            }
            is ChatSubscriptionEvent.Unsubscribe -> {
                logger.debug("on unsubscribe: $event")
                removeSubscription(event.principal.name, event.trackId)
                publishSubscriptionEvent(event)
            }
            is ChatSubscriptionEvent.Disconnect -> {
                logger.debug("on disconnect: $event")
                publishSubscriptionEvent(event)
                removeAllSubscriptions(event.principal.name)
            }
        }
    }


    private fun publishSubscriptionEvent(subscriptionEvent: ChatSubscriptionEvent.Subscribe) =
        sendSubscriptionEvent(subscriptionEvent.principal.name, subscriptionEvent.trackId, EventType.SUBSCRIBE)


    private fun publishSubscriptionEvent(subscriptionEvent: ChatSubscriptionEvent.Unsubscribe) =
        sendSubscriptionEvent(subscriptionEvent.principal.name, subscriptionEvent.trackId, EventType.UNSUBSCRIBE)


    private fun publishSubscriptionEvent(subscriptionEvent: ChatSubscriptionEvent.Disconnect) {
        val userId = subscriptionEvent.principal.name

        subscriptionsByUser[userId]?.forEach { trackId ->
            sendSubscriptionEvent(userId, trackId, EventType.DISCONNECT)
        }
    }


    private fun sendSubscriptionEvent(userId: String, trackId: String, eventType: EventType) {
        val destination = "/topic/$trackId.subscriptions"
        val message = ChatSubscriptionEventOutput(userId, eventType)

        logger.info("Broadcasting to $destination message ${message}")

        messagingTemplate.convertAndSend(destination, message)
    }


    private fun addSubscription(userId: String, trackId: String) = subscriptionsByUser
        .compute(userId) { key: String, value: MutableSet<String>? ->
            if (value == null) {
                val subscriptions = ConcurrentHashMap.newKeySet<String>()
                subscriptions.add(trackId)
                addSubscriberToTrack(userId, trackId)
                subscriptions
            } else {
                value.add(trackId)
                value
            }
        }


    private fun addSubscriberToTrack(userId: String, trackId: String) = subscribersByTrackId
        .compute(trackId) { key: String, value: MutableSet<String>? ->
            if (value == null) {
                val subscribers = ConcurrentHashMap.newKeySet<String>()
                subscribers.add(userId)
                subscribers
            } else {
                value.add(userId)
                value
            }
        }


    private fun removeSubscription(userId: String, trackId: String) {
        subscriptionsByUser[userId]?.remove(trackId)
        subscribersByTrackId[trackId]?.remove(userId)
    }


    private fun removeAllSubscriptions(userId: String) {
        subscriptionsByUser.remove(userId)?.forEach {
            subscribersByTrackId[it]?.remove(userId)
        }
    }


    fun getSubscribers(trackId: String): List<String> =
            subscribersByTrackId[trackId]?.toList().orEmpty()

}