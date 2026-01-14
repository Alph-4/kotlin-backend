package com.example.kotlinbackend.service

import com.example.kotlinbackend.dto.*
import com.example.kotlinbackend.model.User
import com.example.kotlinbackend.repository.UserRepository
import com.example.kotlinbackend.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service d'authentification
 * 
 * Gère l'inscription et la connexion des utilisateurs
 * 
 * @Service : Classe de logique métier Spring
 */
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {
    
    /**
     * Inscrit un nouvel utilisateur
     * 
     * 1. Vérifie que l'email n'existe pas déjà
     * 2. Crypte le mot de passe
     * 3. Crée l'utilisateur en base
     * 4. Génère un token JWT
     * 
     * @param request Les données d'inscription
     * @return AuthResponse Le token et les infos utilisateur
     * @throws RuntimeException Si l'email existe déjà
     */
    fun register(request: RegisterRequest): AuthResponse {
        // Vérification : l'email existe déjà ?
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Un utilisateur avec cet email existe déjà")
        }
        
        // Création de l'utilisateur
        val user = User(
            email = request.email,
            displayName = request.displayName,
            password = passwordEncoder.encode(request.password), // Crypte le mot de passe
            role = request.role  // Utilise le rôle fourni (USER ou ADMIN)
        )
        
        // Sauvegarde en base de données
        val savedUser = userRepository.save(user)
        
        // Génération du token JWT
        val token = jwtUtil.generateToken(savedUser)
        
        // Retourne la réponse avec le token
        return AuthResponse(
            token = token,
            user = savedUser.toUserResponse()
        )
    }
    
    /**
     * Connecte un utilisateur existant
     * 
     * 1. Authentifie l'utilisateur (vérifie email + mot de passe)
     * 2. Génère un token JWT
     * 
     * @param request Les identifiants de connexion
     * @return AuthResponse Le token et les infos utilisateur
     * @throws BadCredentialsException Si les identifiants sont incorrects
     */
    fun login(request: LoginRequest): AuthResponse {
        // Authentification : Spring Security vérifie email + password
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        
        // Si authentification réussie, récupère l'utilisateur
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("Utilisateur non trouvé") }
        
        // Génère le token JWT
        val token = jwtUtil.generateToken(user)
        
        return AuthResponse(
            token = token,
            user = user.toUserResponse()
        )
    }
    
    /**
     * Fonction d'extension pour convertir User en UserResponse
     * (évite d'exposer le mot de passe)
     */
    private fun User.toUserResponse() = UserResponse(
        id = id!!,
        email = email,
        displayName = displayName,
        role = role
    )
}
