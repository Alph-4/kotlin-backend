package com.example.kotlinbackend.repository

import com.example.kotlinbackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository pour l'entité User
 * 
 * JpaRepository fournit automatiquement les méthodes CRUD de base :
 * - save() : Créer ou mettre à jour
 * - findById() : Trouver par ID
 * - findAll() : Récupérer tous les enregistrements
 * - deleteById() : Supprimer par ID
 * - count() : Compter les enregistrements
 * etc.
 * 
 * On peut aussi définir des méthodes personnalisées en suivant
 * une convention de nommage, Spring les implémente automatiquement!
 * 
 * @Repository : Marque cette interface comme un Repository Spring
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    /**
     * Trouve un utilisateur par son email
     * Spring génère automatiquement la requête SQL :
     * SELECT * FROM users WHERE email = ?
     */
    fun findByEmail(email: String): Optional<User>
    
    /**
     * Vérifie si un utilisateur existe avec cet email
     * Spring génère : SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    fun existsByEmail(email: String): Boolean
}
