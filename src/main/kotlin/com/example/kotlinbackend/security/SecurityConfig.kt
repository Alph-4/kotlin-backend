package com.example.kotlinbackend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Configuration de Spring Security
 * 
 * Cette classe configure :
 * 1. Le cryptage des mots de passe (BCrypt)
 * 2. Les règles d'autorisation des endpoints
 * 3. L'authentification JWT (sans session)
 * 4. Le filtre JWT personnalisé
 * 
 * @Configuration : Classe de configuration Spring
 * @EnableWebSecurity : Active la sécurité web
 * @EnableMethodSecurity : Permet l'utilisation d'annotations de sécurité sur les méthodes
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val userDetailsService: UserDetailsService
) {
    
    /**
     * Encoder de mot de passe - BCrypt est un algorithme fort recommandé
     * BCrypt génère automatiquement un "salt" et est résistant aux attaques par force brute
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    
    /**
     * Provider d'authentification
     * Utilise notre UserDetailsService et le PasswordEncoder
     */
    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }
    
    /**
     * Manager d'authentification
     * Nécessaire pour l'authentification lors de la connexion
     */
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
    
    /**
     * Configuration de la chaîne de filtres de sécurité
     * Définit les règles d'accès aux différents endpoints
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Désactive CSRF (pas nécessaire pour une API REST stateless)
            .csrf { it.disable() }
            
            // Configuration des autorisations
            .authorizeHttpRequests { authorize ->
                authorize
                    // Endpoints publics (accessibles sans authentification)
                    .requestMatchers(
                        "/api/auth/**",      // Inscription et connexion
                        "/h2-console/**",    // Console H2 (dev uniquement)
                        "/error"             // Page d'erreur
                    ).permitAll()
                    
                    // Tous les autres endpoints nécessitent une authentification
                    .anyRequest().authenticated()
            }
            
            // Gestion de session : STATELESS (pas de session, uniquement JWT)
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            
            // Ajoute notre filtre JWT avant le filtre d'authentification standard
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            
            // Configure le provider d'authentification
            .authenticationProvider(authenticationProvider())
            
            // Permet l'affichage de la console H2 dans un iframe (dev uniquement)
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }
        
        return http.build()
    }
}
