package spotify_chat.service

import org.springframework.stereotype.Component
import spotify_chat.domain.chat.ChatMessageOutput
import java.util.concurrent.LinkedBlockingQueue

@Component
class ChatMessageService {

    private val history: LinkedBlockingQueue<ChatMessageOutput> = LinkedBlockingQueue(100)

    fun addMessage(message: ChatMessageOutput) {
        history.add(message)
    }

    fun getChatMessages(): Collection<ChatMessageOutput> {
        return history.toTypedArray().toList()
    }

}