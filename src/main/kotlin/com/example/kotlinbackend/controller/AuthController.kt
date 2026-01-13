package com.example.kotlinbackend.controller

import com.example.kotlinbackend.dto.AuthResponse
import com.example.kotlinbackend.dto.LoginRequest
import com.example.kotlinbackend.dto.MessageResponse
import com.example.kotlinbackend.dto.RegisterRequest
import com.example.kotlinbackend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller pour l'authentification
 * 
 * Gère les endpoints d'inscription et de connexion
 * 
 * @RestController : Indique que c'est un controller REST (renvoie du JSON)
 * @RequestMapping : Préfixe tous les endpoints de ce controller avec /api/auth
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    
    /**
     * POST /api/auth/register
     * Inscription d'un nouvel utilisateur
     * 
     * @PostMapping : Gère les requêtes POST
     * @RequestBody : Les données viennent du corps de la requête (JSON)
     * @Valid : Valide les données selon les contraintes définies dans le DTO
     * 
     * Exemple de requête :
     * POST /api/auth/register
     * {
     *   "email": "user@example.com",
     *   "username": "John Doe",
     *   "password": "motdepasse123"
     * }
     * 
     * Réponse :
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIs...",
     *   "type": "Bearer",
     *   "user": {
     *     "id": 1,
     *     "email": "user@example.com",
     *     "username": "John Doe",
     *     "role": "USER"
     *   }
     * }
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        try {
            val response = authService.register(request)
            return ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: Exception) {
            throw RuntimeException("Erreur lors de l'inscription: ${e.message}")
        }
    }
    
    /**
     * POST /api/auth/login
     * Connexion d'un utilisateur existant
     * 
     * Exemple de requête :
     * POST /api/auth/login
     * {
     *   "email": "user@example.com",
     *   "password": "motdepasse123"
     * }
     * 
     * Réponse : Identique à /register
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        try {
            val response = authService.login(request)
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            throw RuntimeException("Identifiants invalides: ${e.message}")
        }
    }
    
    /**
     * GET /api/auth/test
     * Endpoint de test pour vérifier que l'API fonctionne
     */
    @GetMapping("/test")
    fun test(): ResponseEntity<MessageResponse> {
        return ResponseEntity.ok(MessageResponse("L'API fonctionne correctement!"))
    }
}
