package spotify_chat.controller.chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import spotify_chat.domain.chat.ChatHistory
import spotify_chat.service.ChatMessageService


@RestController
@RequestMapping("chat")
class ChatController {

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    @RequestMapping("{trackId}/history")
    fun getChatHistory(@PathVariable("trackId") trackId: String) = ChatHistory(chatMessageService.getChatMessages(trackId).toList())

}