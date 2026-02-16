package com.example.kotlinbackend.metrics

import com.example.kotlinbackend.security.JwtUtil
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class RequestLogWebSocketHandler(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService,
    private val broadcaster: RequestLogBroadcaster
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val token = extractToken(session)
        if (token.isNullOrBlank()) {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        try {
            val username = jwtUtil.extractUsername(token)
            val userDetails = userDetailsService.loadUserByUsername(username)
            if (!jwtUtil.validateToken(token, userDetails)) {
                session.close(CloseStatus.POLICY_VIOLATION)
                return
            }
        } catch (_: Exception) {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        broadcaster.register(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        broadcaster.unregister(session)
    }

    private fun extractToken(session: WebSocketSession): String? {
        val query = session.uri?.query ?: return null
        val pairs = query.split("&")
        for (pair in pairs) {
            val parts = pair.split("=", limit = 2)
            if (parts.size == 2 && parts[0] == "token") {
                return parts[1]
            }
        }
        return null
    }
}
