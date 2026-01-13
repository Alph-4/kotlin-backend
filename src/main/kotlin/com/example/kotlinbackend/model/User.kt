package com.example.kotlinbackend.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Entité User - Représente un utilisateur dans la base de données
 * 
 * Cette classe implémente UserDetails de Spring Security pour gérer l'authentification
 * 
 * @Entity : Indique que c'est une entité JPA (sera mappée en table)
 * @Table : Spécifie le nom de la table dans la base de données
 */
@Entity
@Table(name = "users")
data class User(
    /**
     * ID auto-généré - clé primaire
     * @Id : Marque ce champ comme clé primaire
     * @GeneratedValue : La valeur est générée automatiquement par la BD
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    /**
     * Email de l'utilisateur - doit être unique
     * @Column : Personnalise la colonne en base de données
     */
    @Column(unique = true, nullable = false)
    val email: String,
    
    /**
     * Nom d'utilisateur
     */
    @Column(nullable = false)
    val username: String,
    
    /**
     * Mot de passe crypté (ne jamais stocker en clair!)
     */
    @Column(nullable = false)
    private val password: String,
    
    /**
     * Rôle de l'utilisateur (USER ou ADMIN)
     */
    @Column(nullable = false)
    val role: String = "USER",
    
    /**
     * Liste des todos de cet utilisateur
     * @OneToMany : Un utilisateur peut avoir plusieurs todos
     * mappedBy : Indique le champ dans TodoItem qui fait le lien
     * cascade : Les opérations sur User s'appliquent aux TodoItem
     * orphanRemoval : Supprime les todos orphelins (sans user)
     */
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val todos: MutableList<TodoItem> = mutableListOf()
) : UserDetails {
    
    /**
     * Méthodes de UserDetails pour Spring Security
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_$role"))
    }
    
    override fun getPassword(): String = password
    
    override fun getUsername(): String = email
    
    override fun isAccountNonExpired(): Boolean = true
    
    override fun isAccountNonLocked(): Boolean = true
    
    override fun isCredentialsNonExpired(): Boolean = true
    
    override fun isEnabled(): Boolean = true
}
