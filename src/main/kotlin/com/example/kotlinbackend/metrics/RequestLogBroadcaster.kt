package com.example.kotlinbackend.metrics

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class RequestLogBroadcaster {
    private val sessions = ConcurrentHashMap.newKeySet<WebSocketSession>()

    fun register(session: WebSocketSession) {
        sessions.add(session)
    }

    fun unregister(session: WebSocketSession) {
        sessions.remove(session)
    }

    fun broadcast(payload: String) {
        val message = TextMessage(payload)
        val iterator = sessions.iterator()
        while (iterator.hasNext()) {
            val session = iterator.next()
            if (!session.isOpen) {
                iterator.remove()
                continue
            }
            try {
                session.sendMessage(message)
            } catch (_: Exception) {
                iterator.remove()
            }
        }
    }
}
