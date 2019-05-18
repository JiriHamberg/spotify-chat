package spotify_chat.domain.chat

import org.springframework.context.ApplicationEvent
import java.security.Principal


sealed class ChatSubscriptionEvent(source: Any) : ApplicationEvent(source) {

    data class Subscribe(
        val source_: Any,
        val principal: Principal,
        val trackId: String
    ): ChatSubscriptionEvent(source_)

    data class Unsubscribe(
        val source_: Any,
        val principal: Principal,
        val trackId: String
    ): ChatSubscriptionEvent(source_)

    data class Disconnect(
        val source_: Any,
        val principal: Principal
    ): ChatSubscriptionEvent(source_)

}