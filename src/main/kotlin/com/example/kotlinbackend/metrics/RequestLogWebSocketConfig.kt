package com.example.kotlinbackend.metrics

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class RequestLogWebSocketConfig(
    private val requestLogWebSocketHandler: RequestLogWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(requestLogWebSocketHandler, "/ws/requests")
            .setAllowedOrigins(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173",
                "http://localhost:5174",
                "https://cuddly-goggles-957q767rpw6c9p7p.github.dev"
            )
    }
}
