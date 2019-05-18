package spotify_chat.controller.chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import spotify_chat.domain.chat.ChatHistory
import spotify_chat.domain.chat.Subscribers
import spotify_chat.service.ChatMessageService
import spotify_chat.service.ChatSubscriberService


@RestController
@RequestMapping("chat")
class ChatController {

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    @Autowired
    private lateinit var chatSubscriberService: ChatSubscriberService


    @GetMapping("{trackId}/history")
    fun getChatHistory(@PathVariable("trackId") trackId: String) =
        ChatHistory(chatMessageService.getChatMessages(trackId).toList())


    @GetMapping("{trackId}/subscriptions")
    fun getChatSubscriptions(@PathVariable("trackId") trackId: String) =
        Subscribers(trackId, chatSubscriberService.getSubscribers(trackId))

}