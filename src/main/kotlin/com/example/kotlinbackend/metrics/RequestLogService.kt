package com.example.kotlinbackend.metrics

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class RequestLogService {
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
    }

    fun nextId(): Long = counter.getAndIncrement()

    fun list(limit: Int?): List<RequestLog> {
        synchronized(lock) {
            val max = limit ?: logs.size
            return logs.take(max)
        }
    }
}
