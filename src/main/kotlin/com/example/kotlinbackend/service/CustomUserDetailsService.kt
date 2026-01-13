package com.example.kotlinbackend.service

import com.example.kotlinbackend.model.User
import com.example.kotlinbackend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Service pour charger les détails de l'utilisateur
 * 
 * Implémente UserDetailsService de Spring Security
 * Utilisé par Spring Security pour charger un utilisateur lors de l'authentification
 * 
 * @Service : Marque cette classe comme un service Spring (logique métier)
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    /**
     * Charge un utilisateur par son nom d'utilisateur (ici l'email)
     * 
     * Cette méthode est appelée automatiquement par Spring Security
     * lors de l'authentification
     * 
     * @param username L'email de l'utilisateur
     * @return UserDetails Les détails de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas
     */
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)
            .orElseThrow { 
                UsernameNotFoundException("Utilisateur non trouvé avec l'email: $username") 
            }
    }
    
    /**
     * Charge un utilisateur par son ID
     * Utile pour récupérer l'utilisateur authentifié
     */
    fun loadUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { 
                UsernameNotFoundException("Utilisateur non trouvé avec l'ID: $id") 
            }
    }
}
