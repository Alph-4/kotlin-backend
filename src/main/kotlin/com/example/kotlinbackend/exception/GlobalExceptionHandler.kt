package com.example.kotlinbackend.exception

import com.example.kotlinbackend.dto.MessageResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Gestionnaire global des exceptions
 * 
 * Intercepte les exceptions levées dans l'application
 * et retourne des réponses HTTP appropriées
 * 
 * @RestControllerAdvice : S'applique à tous les controllers
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    
    /**
     * Gère les exceptions générales (RuntimeException)
     * Retourne un code 400 Bad Request
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<MessageResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(MessageResponse(ex.message ?: "Une erreur est survenue"))
    }
    
    /**
     * Gère les erreurs d'authentification (mauvais identifiants)
     * Retourne un code 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<MessageResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(MessageResponse("Email ou mot de passe incorrect"))
    }
    
    /**
     * Gère les cas où un utilisateur n'est pas trouvé
     * Retourne un code 404 Not Found
     */
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUserNotFound(ex: UsernameNotFoundException): ResponseEntity<MessageResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(MessageResponse(ex.message ?: "Utilisateur non trouvé"))
    }
    
    /**
     * Gère toutes les autres exceptions non prévues
     * Retourne un code 500 Internal Server Error
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<MessageResponse> {
        // En production, ne pas exposer le détail de l'erreur
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(MessageResponse("Erreur interne du serveur"))
    }
}
