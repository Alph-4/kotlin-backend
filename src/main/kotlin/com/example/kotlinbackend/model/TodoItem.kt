package com.example.kotlinbackend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entité TodoItem - Représente une tâche dans une todo list
 * 
 * @Entity : Marque cette classe comme entité JPA
 * @Table : Spécifie le nom de la table
 */
@Entity
@Table(name = "todo_items")
data class TodoItem(
    /**
     * ID auto-généré
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    /**
     * Titre de la tâche
     */
    @Column(nullable = false)
    var title: String,
    
    /**
     * Description détaillée de la tâche
     */
    @Column(length = 1000)
    var description: String? = null,
    
    /**
     * Statut de la tâche (complétée ou non)
     */
    @Column(nullable = false)
    var completed: Boolean = false,
    
    /**
     * Date de création
     */
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    /**
     * Date de dernière modification
     */
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    /**
     * Utilisateur propriétaire de cette tâche
     * @ManyToOne : Plusieurs todos peuvent appartenir à un utilisateur
     * @JoinColumn : Spécifie la colonne de jointure (clé étrangère)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)
