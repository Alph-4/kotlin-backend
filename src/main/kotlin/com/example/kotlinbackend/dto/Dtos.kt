package com.example.kotlinbackend.dto

/**
 * DTOs (Data Transfer Objects) - Objets utilisés pour transférer des données
 * entre le client et le serveur sans exposer directement les entités
 */

/**
 * Requête pour l'inscription d'un nouvel utilisateur
 */
data class RegisterRequest(
    val email: String,
    val displayName: String,
    val password: String
)

/**
 * Requête pour la connexion
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Réponse contenant le token JWT après authentification
 */
data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val user: UserResponse
)

/**
 * Représentation d'un utilisateur pour les réponses API
 * (sans le mot de passe!)
 */
data class UserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val role: String
)

/**
 * Requête pour créer ou mettre à jour une tâche
 */
data class TodoRequest(
    val title: String,
    val description: String? = null,
    val completed: Boolean = false
)

/**
 * Représentation d'une tâche pour les réponses API
 */
data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Réponse générique pour les messages de succès/erreur
 */
data class MessageResponse(
    val message: String
)
