package com.example.kotlinbackend.metrics

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Component
class RequestLogFilter(
    private val requestLogService: RequestLogService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = System.currentTimeMillis()
        filterChain.doFilter(request, response)
        val duration = System.currentTimeMillis() - start

        val auth = SecurityContextHolder.getContext().authentication
        val user = auth?.name ?: "anonymous"

        val log = RequestLog(
            id = requestLogService.nextId(),
            timestamp = Instant.now(),
            method = request.method,
            path = request.requestURI,
            status = response.status,
            durationMs = duration,
            user = user,
            ip = request.remoteAddr ?: "unknown"
        )

        requestLogService.add(log)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/h2-console") ||
            path.startsWith("/error") ||
            path.startsWith("/api/metrics/requests")
    }
}
