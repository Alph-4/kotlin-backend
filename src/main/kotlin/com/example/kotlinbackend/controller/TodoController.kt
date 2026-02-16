package com.example.kotlinbackend.controller

import com.example.kotlinbackend.dto.MessageResponse
import com.example.kotlinbackend.dto.TodoRequest
import com.example.kotlinbackend.dto.TodoResponse
import com.example.kotlinbackend.service.TodoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Controller pour la gestion des todos
 * 
 * Endpoints CRUD complets pour les tâches
 * Tous les endpoints nécessitent une authentification
 */
@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService
) {
    
    /**
     * POST /api/todos
     * Crée une nouvelle tâche
     * 
     * En-tête requis :
     * Authorization: Bearer <token>
     * 
     * Corps de la requête :
     * {
     *   "title": "Faire les courses",
     *   "description": "Acheter du pain et du lait",
     *   "completed": false
     * }
     * 
     * Réponse (201 Created) :
     * {
     *   "id": 1,
     *   "title": "Faire les courses",
     *   "description": "Acheter du pain et du lait",
     *   "completed": false,
     *   "createdAt": "2024-01-13T10:30:00",
     *   "updatedAt": "2024-01-13T10:30:00"
     * }
     */
    @PostMapping
    fun createTodo(@Valid @RequestBody request: TodoRequest): ResponseEntity<TodoResponse> {
        val todo = todoService.createTodo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(todo)
    }
    
    /**
     * GET /api/todos
     * Récupère toutes les tâches de l'utilisateur authentifié
     * 
     * Paramètre optionnel : ?completed=true ou ?completed=false
     * pour filtrer par statut
     * 
     * Exemples :
     * - GET /api/todos (toutes les tâches)
     * - GET /api/todos?completed=true (seulement les complétées)
     * - GET /api/todos?completed=false (seulement les non complétées)
     * 
     * @RequestParam(required = false) : Paramètre optionnel
     */
    @GetMapping
    fun getAllTodos(
        @RequestParam(required = false) completed: Boolean?
    ): ResponseEntity<List<TodoResponse>> {
        val todos = todoService.getAllTodos(completed)
        return ResponseEntity.ok(todos)
    }
    
    /**
     * GET /api/todos/{id}
     * Récupère une tâche spécifique
     * 
     * Exemple : GET /api/todos/1
     * 
     * Réponse : Détails de la tâche (même format que POST)
     */
    @GetMapping("/{id}")
    fun getTodoById(@PathVariable id: Long): ResponseEntity<TodoResponse> {
        val todo = todoService.getTodoById(id)
        return ResponseEntity.ok(todo)
    }
    
    /**
     * PUT /api/todos/{id}
     * Met à jour une tâche existante
     * 
     * Exemple : PUT /api/todos/1
     * 
     * Corps de la requête :
     * {
     *   "title": "Faire les courses (modifié)",
     *   "description": "Acheter du pain, du lait et des œufs",
     *   "completed": true
     * }
     * 
     * Réponse : La tâche mise à jour
     */
    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable id: Long,
        @Valid @RequestBody request: TodoRequest
    ): ResponseEntity<TodoResponse> {
        val todo = todoService.updateTodo(id, request)
        return ResponseEntity.ok(todo)
    }
    
    /**
     * DELETE /api/todos/{id}
     * Supprime une tâche
     * 
     * Exemple : DELETE /api/todos/1
     * 
     * Réponse :
     * {
     *   "message": "Tâche supprimée avec succès"
     * }
     */
    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<MessageResponse> {
        todoService.deleteTodo(id)
        return ResponseEntity.ok(MessageResponse("Tâche supprimée avec succès"))
    }

    /**
    *GET /api/all-todos
        * Récupère toutes les tâches de tous les utilisateurs (ADMIN uniquement)
        * 
        * Exemple : GET /api/all-todos
        * 
        * Réponse : Liste de toutes les tâches de tous les utilisateurs
        */      
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllTodos(): ResponseEntity<List<TodoResponse>> {
        val todos = todoService.getAllTodos(askedByAdmin = true)
        return ResponseEntity.ok(todos)
    }                        
    
}
