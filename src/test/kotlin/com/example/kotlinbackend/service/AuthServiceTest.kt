package com.example.kotlinbackend.service

import com.example.kotlinbackend.model.User
import com.example.kotlinbackend.repository.UserRepository
import com.example.kotlinbackend.security.JwtUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthServiceTest {
    
    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = mock(PasswordEncoder::class.java)
    private val jwtUtil = mock(JwtUtil::class.java)
    
    private val authService = AuthService(userRepository, passwordEncoder, jwtUtil)
    
    @Test
    fun `register should create new user`() {
        // Given
        val email = "test@example.com"
        `when`(userRepository.existsByEmail(email)).thenReturn(false)
        `when`(passwordEncoder.encode(any())).thenReturn("hashedPassword")
        
        // When
        val request = RegisterRequest(email, "password", "Test User")
        authService.register(request)
        
        // Then
        verify(userRepository, times(1)).save(any())
    }
}