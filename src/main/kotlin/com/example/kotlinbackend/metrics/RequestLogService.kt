package com.example.kotlinbackend.metrics

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class RequestLogService(
    private val objectMapper: ObjectMapper,
    private val broadcaster: RequestLogBroadcaster
) {
    private val counter = AtomicLong(1)
    private val maxEntries = 300
    private val logs = ArrayDeque<RequestLog>()
    private val lock = Any()

    fun add(log: RequestLog) {
        synchronized(lock) {
            logs.addFirst(log)
            while (logs.size > maxEntries) {
                logs.removeLast()
            }
        }

        val payload = objectMapper.writeValueAsString(toResponse(log))
        broadcaster.broadcast(payload)
    }

    fun nextId(): Long = counter.getAndIncrement()

    fun list(limit: Int?): List<RequestLog> {
        synchronized(lock) {
            val max = limit ?: logs.size
            return logs.take(max)
        }
    }

    fun listResponses(limit: Int?): List<RequestLogResponse> {
        return list(limit).map { toResponse(it) }
    }

    private fun toResponse(log: RequestLog): RequestLogResponse {
        return RequestLogResponse(
            id = log.id,
            timestamp = log.timestamp.toString(),
            method = log.method,
            path = log.path,
            status = log.status,
            durationMs = log.durationMs,
            user = log.user,
            ip = log.ip
        )
    }
}
