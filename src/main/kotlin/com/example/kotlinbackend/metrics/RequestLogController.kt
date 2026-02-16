package com.example.kotlinbackend.metrics

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/metrics")
class RequestLogController(
    private val requestLogService: RequestLogService
) {

    @GetMapping("/requests")
    fun getRequests(
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<RequestLogResponse>> {
        val logs = requestLogService.list(limit)
        val response = logs.map {
            RequestLogResponse(
                id = it.id,
                timestamp = it.timestamp.toString(),
                method = it.method,
                path = it.path,
                status = it.status,
                durationMs = it.durationMs,
                user = it.user,
                ip = it.ip
            )
        }
        return ResponseEntity.ok(response)
    }
}
