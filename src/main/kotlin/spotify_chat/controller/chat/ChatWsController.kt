package spotify_chat.controller.chat

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import spotify_chat.domain.chat.ChatMessage
import spotify_chat.domain.chat.ChatMessageOutput
import spotify_chat.service.ChatMessageService
import java.util.*

@Controller
class ChatWsController {

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("/chat/{trackId}")
    @SendTo("/topic/{trackId}/messages")
    fun send(@DestinationVariable("trackId") trackId: String, @Payload message: ChatMessage, auth: Authentication): ChatMessageOutput {
        val outMessage = ChatMessageOutput(UUID.randomUUID().toString(), message.body, auth.principal as String)
        chatMessageService.addMessage(trackId, outMessage)
        return outMessage
    }


}