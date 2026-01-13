package com.example.kotlinbackend.repository

import com.example.kotlinbackend.model.TodoItem
import com.example.kotlinbackend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository pour l'entité TodoItem
 * 
 * Fournit les opérations CRUD pour les tâches
 */
@Repository
interface TodoRepository : JpaRepository<TodoItem, Long> {
    
    /**
     * Trouve toutes les tâches d'un utilisateur spécifique
     * Spring génère : SELECT * FROM todo_items WHERE user_id = ?
     */
    fun findByUser(user: User): List<TodoItem>
    
    /**
     * Trouve toutes les tâches d'un utilisateur avec un certain statut
     * Spring génère : SELECT * FROM todo_items WHERE user_id = ? AND completed = ?
     */
    fun findByUserAndCompleted(user: User, completed: Boolean): List<TodoItem>
}
