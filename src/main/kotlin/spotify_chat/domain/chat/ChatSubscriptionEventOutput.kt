package spotify_chat.domain.chat

import java.time.ZonedDateTime


enum class EventType {
    SUBSCRIBE, UNSUBSCRIBE, DISCONNECT
}

data class ChatSubscriptionEventOutput(
    val userId: String,
    val eventType: EventType,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)