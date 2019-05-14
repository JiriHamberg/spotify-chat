package spotify_chat.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spotify_chat.domain.chat.ChatMessageOutput
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

@Component
class ChatMessageService {

    private val CHAT_HISTORY_CAPASITY = 100

    private val history: ConcurrentHashMap<String, LinkedBlockingQueue<ChatMessageOutput>> = ConcurrentHashMap()

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun addMessage(trackId: String, message: ChatMessageOutput) = history
            .compute(trackId) { key: String, value: LinkedBlockingQueue<ChatMessageOutput>? ->
                if (value == null) {
                    val queue = LinkedBlockingQueue<ChatMessageOutput>(CHAT_HISTORY_CAPASITY)
                    queue.add(message)
                    queue
                } else {
                    value.add(message)
                    value
                }
            }


    fun getChatMessages(trackId: String): Collection<ChatMessageOutput> = history
            .getOrDefault(trackId, LinkedBlockingQueue())
            .toTypedArray()
            .toList()

}