package com.example.kotlinbackend.service

import com.example.kotlinbackend.dto.UserResponse
import com.example.kotlinbackend.model.User
import com.example.kotlinbackend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Service pour gérer les utilisateurs (CRUD)
 * 
 * Fournit les opérations de lecture, mise à jour et suppression des utilisateurs
 * (La création est gérée par AuthService via l'inscription)
 */
@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    /**
     * Récupère l'utilisateur actuellement authentifié
     * 
     * Utilise le SecurityContext de Spring Security qui contient
     * l'utilisateur authentifié par le filtre JWT
     * 
     * @return User L'utilisateur authentifié
     */
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as User
    }
    
    /**
     * Récupère tous les utilisateurs
     * 
     * @return List<UserResponse> Liste de tous les utilisateurs
     */
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toUserResponse() }
    }
    
    /**
     * Récupère un utilisateur par son ID
     * 
     * @param id L'ID de l'utilisateur
     * @return UserResponse Les données de l'utilisateur
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("Utilisateur non trouvé avec l'ID: $id") }
        return user.toUserResponse()
    }
    
    /**
     * Supprime un utilisateur
     * 
     * @param id L'ID de l'utilisateur à supprimer
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw RuntimeException("Utilisateur non trouvé avec l'ID: $id")
        }
        userRepository.deleteById(id)
    }
    
    /**
     * Fonction d'extension pour convertir User en UserResponse
     */
    private fun User.toUserResponse() = UserResponse(
        id = id!!,
        email = email,
        username = username,
        role = role
    )
}
