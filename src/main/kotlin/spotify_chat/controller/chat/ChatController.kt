package spotify_chat.controller.chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spotify_chat.domain.chat.ChatHistory
import spotify_chat.service.ChatMessageService


@RestController
@RequestMapping("chat")
class ChatController {

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    @RequestMapping("history")
    fun getChatHistory() = ChatHistory(chatMessageService.getChatMessages().toList())

}