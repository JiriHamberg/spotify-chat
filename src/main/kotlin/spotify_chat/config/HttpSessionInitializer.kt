package spotify_chat.config

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer

class HttpSessionInitializer : AbstractHttpSessionApplicationInitializer {

    constructor() : super(SessionConfig::class.java)

}