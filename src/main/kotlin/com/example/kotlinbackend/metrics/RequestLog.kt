package com.example.kotlinbackend.metrics

import java.time.Instant

/**
 * Representation of a single HTTP request.
 */
data class RequestLog(
    val id: Long,
    val timestamp: Instant,
    val method: String,
    val path: String,
    val status: Int,
    val durationMs: Long,
    val user: String,
    val ip: String
)

/**
 * API response shape for request logs.
 */
data class RequestLogResponse(
    val id: Long,
    val timestamp: String,
    val method: String,
    val path: String,
    val status: Int,
    val durationMs: Long,
    val user: String,
    val ip: String
)
