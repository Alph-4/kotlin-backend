package com.example.kotlinbackend.service

import com.example.kotlinbackend.dto.TodoRequest
import com.example.kotlinbackend.dto.TodoResponse
import com.example.kotlinbackend.model.TodoItem
import com.example.kotlinbackend.model.User
import com.example.kotlinbackend.repository.TodoRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service pour gérer les todos (CRUD complet)
 * 
 * Fournit toutes les opérations sur les tâches :
 * - Créer une tâche
 * - Lire une ou plusieurs tâches
 * - Mettre à jour une tâche
 * - Supprimer une tâche
 */
@Service
class TodoService(
    private val todoRepository: TodoRepository,
    private val userService: UserService
) {
    
    /**
     * Crée une nouvelle tâche pour l'utilisateur authentifié
     * 
     * @param request Les données de la tâche
     * @return TodoResponse La tâche créée
     */
    fun createTodo(request: TodoRequest): TodoResponse {
        val currentUser = userService.getCurrentUser()
        
        val todo = TodoItem(
            title = request.title,
            description = request.description,
            completed = request.completed,
            user = currentUser
        )
        
        val savedTodo = todoRepository.save(todo)
        return savedTodo.toTodoResponse()
    }
    
    /**
     * Récupère toutes les tâches de l'utilisateur authentifié
     * 
     * @param completed Optionnel : filtre par statut (complété ou non)
     * @return List<TodoResponse> Liste des tâches
     */
    fun getAllTodos(completed: Boolean? = null, askedByAdmin: Boolean = false): List<TodoResponse> {
        val currentUser = userService.getCurrentUser()
        
          val todos: List<TodoItem>

        if(askedByAdmin){
           // Si l'admin demande, on retourne toutes les tâches de tous les utilisateurs        
           todos = todoRepository.findAll()
        }
        else if (completed != null) {
            // Filtre par statut si spécifié
            todos = todoRepository.findByUserAndCompleted(currentUser, completed)
        } else {
            // Sinon, retourne toutes les tâches
            todos =     todoRepository.findByUser(currentUser)
        }
        
        return todos.map { it.toTodoResponse() }
    }
    
    /**
     * Récupère une tâche par son ID
     * 
     * Vérifie que la tâche appartient bien à l'utilisateur authentifié
     * 
     * @param id L'ID de la tâche
     * @return TodoResponse La tâche
     * @throws RuntimeException Si la tâche n'existe pas ou n'appartient pas à l'utilisateur
     */
    fun getTodoById(id: Long): TodoResponse {
        val todo = findTodoByIdAndUser(id)
        return todo.toTodoResponse()
    }
    
    /**
     * Met à jour une tâche existante
     * 
     * @param id L'ID de la tâche
     * @param request Les nouvelles données
     * @return TodoResponse La tâche mise à jour
     * @throws RuntimeException Si la tâche n'existe pas ou n'appartient pas à l'utilisateur
     */
    fun updateTodo(id: Long, request: TodoRequest): TodoResponse {
        val todo = findTodoByIdAndUser(id)
        
        // Mise à jour des champs
        todo.title = request.title
        todo.description = request.description
        todo.completed = request.completed
        todo.updatedAt = LocalDateTime.now()
        
        val updatedTodo = todoRepository.save(todo)
        return updatedTodo.toTodoResponse()
    }
    
    /**
     * Supprime une tâche
     * 
     * @param id L'ID de la tâche à supprimer
     * @throws RuntimeException Si la tâche n'existe pas ou n'appartient pas à l'utilisateur
     */
    fun deleteTodo(id: Long) {
        val todo = findTodoByIdAndUser(id)
        todoRepository.delete(todo)
    }
    
    /**
     * Méthode privée : trouve une tâche et vérifie qu'elle appartient à l'utilisateur
     */
    private fun findTodoByIdAndUser(id: Long): TodoItem {
        val currentUser = userService.getCurrentUser()
        val todo = todoRepository.findById(id)
            .orElseThrow { RuntimeException("Tâche non trouvée avec l'ID: $id") }
        
        // Sécurité : vérifie que la tâche appartient bien à l'utilisateur
        if (todo.user.id != currentUser.id) {
            throw RuntimeException("Vous n'avez pas accès à cette tâche")
        }
        
        return todo
    }
    
    /**
     * Fonction d'extension pour convertir TodoItem en TodoResponse
     */
    private fun TodoItem.toTodoResponse() = TodoResponse(
        id = id!!,
        title = title,
        description = description,
        completed = completed,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
