package com.example.kotlinbackend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filtre JWT - Intercepte chaque requête pour vérifier le token
 * 
 * OncePerRequestFilter : Garantit que le filtre s'exécute une seule fois par requête
 * 
 * Ce filtre :
 * 1. Extrait le token JWT de l'en-tête Authorization
 * 2. Valide le token
 * 3. Authentifie l'utilisateur si le token est valide
 * 
 * @Component : Bean Spring géré automatiquement
 */
@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    
    /**
     * Méthode appelée pour chaque requête HTTP
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 1. Extraire le token de l'en-tête Authorization
            val jwt = extractJwtFromRequest(request)
            
            // 2. Si un token existe et qu'aucun utilisateur n'est déjà authentifié
            if (jwt != null && SecurityContextHolder.getContext().authentication == null) {
                // Extraire l'email du token
                val userEmail = jwtUtil.extractUsername(jwt)
                
                // Charger les détails de l'utilisateur depuis la base de données
                val userDetails = userDetailsService.loadUserByUsername(userEmail)
                
                // 3. Valider le token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Créer un objet d'authentification
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    
                    // Ajouter les détails de la requête
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    
                    // 4. Définir l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (ex: Exception) {
            logger.error("Impossible d'authentifier l'utilisateur: ${ex.message}")
        }
        
        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response)
    }
    
    /**
     * Extrait le token JWT de l'en-tête Authorization
     * Format attendu : "Bearer <token>"
     */
    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7) // Enlève "Bearer "
        }
        
        return null
    }
}
