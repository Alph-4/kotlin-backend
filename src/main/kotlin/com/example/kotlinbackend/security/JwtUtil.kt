package com.example.kotlinbackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * Utilitaire pour gérer les tokens JWT (JSON Web Tokens)
 * 
 * JWT est un standard pour créer des tokens d'accès sécurisés.
 * Il contient trois parties encodées en Base64 :
 * 1. Header : Type de token et algorithme de signature
 * 2. Payload : Les données (claims) comme l'email, l'expiration
 * 3. Signature : Pour vérifier l'intégrité du token
 * 
 * @Component : Marque cette classe comme un bean Spring
 */
@Component
class JwtUtil {
    
    /**
     * Clé secrète pour signer les tokens
     * @Value : Injecte la valeur depuis application.yml
     */
    @Value("\${jwt.secret}")
    private lateinit var secret: String
    
    /**
     * Durée de validité du token en millisecondes
     */
    @Value("\${jwt.expiration}")
    private var expiration: Long = 86400000 // 24 heures par défaut
    
    /**
     * Génère la clé de signature à partir du secret
     */
    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }
    
    /**
     * Génère un token JWT pour un utilisateur
     * 
     * @param userDetails Les détails de l'utilisateur
     * @return Le token JWT
     */
    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return createToken(claims, userDetails.username)
    }
    
    /**
     * Crée le token avec les claims et le sujet (email)
     */
    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Date()
        val expirationDate = Date(now.time + expiration)
        
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(getSigningKey())
            .compact()
    }
    
    /**
     * Extrait le nom d'utilisateur (email) du token
     */
    fun extractUsername(token: String): String {
        return extractClaim(token) { it.subject }
    }
    
    /**
     * Extrait la date d'expiration du token
     */
    fun extractExpiration(token: String): Date {
        return extractClaim(token) { it.expiration }
    }
    
    /**
     * Extrait un claim spécifique du token
     */
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }
    
    /**
     * Extrait tous les claims du token
     */
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
    
    /**
     * Vérifie si le token est expiré
     */
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
    
    /**
     * Valide le token
     * Vérifie que le token correspond à l'utilisateur et n'est pas expiré
     */
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }
}
