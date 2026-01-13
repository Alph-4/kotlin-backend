package com.example.kotlinbackend.controller

import com.example.kotlinbackend.dto.MessageResponse
import com.example.kotlinbackend.dto.UserResponse
import com.example.kotlinbackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Controller pour la gestion des utilisateurs
 * 
 * Endpoints CRUD pour les utilisateurs
 * Tous les endpoints nécessitent une authentification
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    /**
     * GET /api/users/me
     * Récupère les informations de l'utilisateur authentifié
     * 
     * En-tête requis :
     * Authorization: Bearer <token>
     * 
     * Réponse :
     * {
     *   "id": 1,
     *   "email": "user@example.com",
     *   "username": "John Doe",
     *   "role": "USER"
     * }
     */
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val user = userService.getCurrentUser()
        return ResponseEntity.ok(UserResponse(
            id = user.id!!,
            email = user.email,
            username = user.username,
            role = user.role
        ))
    }
    
    /**
     * GET /api/users
     * Récupère tous les utilisateurs
     * 
     * Nécessite le rôle ADMIN
     * @PreAuthorize : Vérifie l'autorisation avant d'exécuter la méthode
     * 
     * Réponse : Liste d'utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }
    
    /**
     * GET /api/users/{id}
     * Récupère un utilisateur par son ID
     * 
     * @PathVariable : Extrait l'ID depuis l'URL
     * 
     * Exemple : GET /api/users/5
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }
    
    /**
     * DELETE /api/users/{id}
     * Supprime un utilisateur
     * 
     * Nécessite le rôle ADMIN
     * 
     * Exemple : DELETE /api/users/5
     * 
     * Réponse :
     * {
     *   "message": "Utilisateur supprimé avec succès"
     * }
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<MessageResponse> {
        userService.deleteUser(id)
        return ResponseEntity.ok(MessageResponse("Utilisateur supprimé avec succès"))
    }
}
