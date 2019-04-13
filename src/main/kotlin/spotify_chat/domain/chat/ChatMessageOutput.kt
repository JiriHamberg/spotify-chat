package spotify_chat.domain.chat

import java.time.ZonedDateTime

data class ChatMessageOutput(
    val body: String,
    val userId: String,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)