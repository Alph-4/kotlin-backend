# Backend Kotlin avec Spring Boot

## 📚 Description

Ce projet est un backend REST API complet développé en Kotlin avec Spring Boot. Il implémente :

- **Authentification JWT** (JSON Web Tokens)
- **CRUD Utilisateurs** (inscription, connexion, gestion)
- **CRUD Todo List** (créer, lire, modifier, supprimer des tâches)
- **Sécurité** avec Spring Security
- **Base de données** H2 en mémoire (facile à remplacer par PostgreSQL/MySQL)

## 🛠️ Technologies utilisées

- **Kotlin** 1.9.21
- **Spring Boot** 3.2.1
- **Spring Security** (authentification et autorisation)
- **Spring Data JPA** (persistance des données)
- **JWT** (io.jsonwebtoken:jjwt)
- **H2 Database** (base de données en mémoire)
- **Gradle** (gestion des dépendances)

## 📁 Structure du projet

```
src/main/kotlin/com/example/kotlinbackend/
├── KotlinBackendApplication.kt      # Point d'entrée de l'application
├── controller/                       # Controllers REST (API endpoints)
│   ├── AuthController.kt            # Inscription et connexion
│   ├── UserController.kt            # CRUD utilisateurs
│   └── TodoController.kt            # CRUD todos
├── service/                          # Logique métier
│   ├── AuthService.kt               # Service d'authentification
│   ├── UserService.kt               # Service utilisateurs
│   ├── TodoService.kt               # Service todos
│   └── CustomUserDetailsService.kt  # Chargement des utilisateurs
├── repository/                       # Accès aux données (JPA)
│   ├── UserRepository.kt            # Repository utilisateurs
│   └── TodoRepository.kt            # Repository todos
├── model/                            # Entités JPA
│   ├── User.kt                      # Entité utilisateur
│   └── TodoItem.kt                  # Entité tâche
├── dto/                              # Data Transfer Objects
│   └── Dtos.kt                      # Requêtes et réponses API
├── security/                         # Configuration de sécurité
│   ├── SecurityConfig.kt            # Configuration Spring Security
│   ├── JwtUtil.kt                   # Utilitaire JWT
│   └── JwtAuthenticationFilter.kt   # Filtre d'authentification JWT
└── exception/                        # Gestion des erreurs
    └── GlobalExceptionHandler.kt    # Gestionnaire d'exceptions global
```

## 🚀 Démarrage rapide

### Prérequis

- JDK 17 ou supérieur
- Gradle (ou utilisez le wrapper fourni)

### Installation et lancement

```bash
# 1. Compiler le projet
./gradlew build

# 2. Lancer l'application
./gradlew bootRun
```

L'application sera accessible sur : http://localhost:8080

### Console H2 Database

Pour visualiser la base de données pendant le développement :

- URL : http://localhost:8080/h2-console
- JDBC URL : `jdbc:h2:mem:testdb`
- Username : `sa`
- Password : (vide)

## 📖 Guide d'utilisation de l'API

### 1. Inscription d'un utilisateur

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "John Doe",
  "password": "motdepasse123"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "John Doe",
    "role": "USER"
  }
}
```

### 2. Connexion

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "motdepasse123"
}
```

**Réponse :** Identique à l'inscription

### 3. Utiliser le token JWT

Pour tous les endpoints protégés, ajoutez le token dans l'en-tête :

```
Authorization: Bearer <votre_token>
```

### 4. CRUD Utilisateurs

#### Obtenir l'utilisateur actuel
```bash
GET http://localhost:8080/api/users/me
Authorization: Bearer <token>
```

#### Obtenir tous les utilisateurs (ADMIN uniquement)
```bash
GET http://localhost:8080/api/users
Authorization: Bearer <token>
```

#### Obtenir un utilisateur par ID
```bash
GET http://localhost:8080/api/users/1
Authorization: Bearer <token>
```

#### Supprimer un utilisateur (ADMIN uniquement)
```bash
DELETE http://localhost:8080/api/users/1
Authorization: Bearer <token>
```

### 5. CRUD Todo List

#### Créer une tâche
```bash
POST http://localhost:8080/api/todos
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Faire les courses",
  "description": "Acheter du pain et du lait",
  "completed": false
}
```

#### Obtenir toutes les tâches
```bash
# Toutes les tâches
GET http://localhost:8080/api/todos
Authorization: Bearer <token>

# Seulement les complétées
GET http://localhost:8080/api/todos?completed=true

# Seulement les non complétées
GET http://localhost:8080/api/todos?completed=false
```

#### Obtenir une tâche par ID
```bash
GET http://localhost:8080/api/todos/1
Authorization: Bearer <token>
```

#### Mettre à jour une tâche
```bash
PUT http://localhost:8080/api/todos/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Faire les courses (modifié)",
  "description": "Acheter du pain, du lait et des œufs",
  "completed": true
}
```

#### Supprimer une tâche
```bash
DELETE http://localhost:8080/api/todos/1
Authorization: Bearer <token>
```

## 🔒 Sécurité

### JWT (JSON Web Tokens)

L'application utilise JWT pour l'authentification :

1. L'utilisateur s'inscrit ou se connecte
2. Le serveur génère un token JWT signé
3. Le client envoie ce token dans l'en-tête `Authorization` pour chaque requête
4. Le serveur vérifie et valide le token

### Cryptage des mots de passe

Les mots de passe sont cryptés avec **BCrypt** avant d'être stockés en base de données. BCrypt :
- Génère automatiquement un "salt" unique
- Est résistant aux attaques par force brute
- Est recommandé par OWASP

### Autorisation par rôles

Certains endpoints nécessitent le rôle ADMIN :
- `GET /api/users` : Liste tous les utilisateurs
- `DELETE /api/users/{id}` : Supprime un utilisateur

## 🎓 Concepts clés à comprendre

### 1. Architecture en couches

Le projet suit une architecture MVC (Model-View-Controller) adaptée au REST :

- **Controller** : Reçoit les requêtes HTTP, valide les données
- **Service** : Contient la logique métier
- **Repository** : Accède à la base de données
- **Model** : Représente les entités de la base de données

### 2. Injection de dépendances

Spring gère automatiquement la création et l'injection des beans :

```kotlin
@Service
class TodoService(
    private val todoRepository: TodoRepository, // Injecté automatiquement
    private val userService: UserService        // Injecté automatiquement
)
```

### 3. JPA et relations

- **@OneToMany** : Un utilisateur a plusieurs todos
- **@ManyToOne** : Un todo appartient à un utilisateur
- **cascade** : Les opérations se propagent (ex: supprimer un user supprime ses todos)

### 4. Spring Security

- **SecurityConfig** : Configure les règles d'accès
- **JwtAuthenticationFilter** : Intercepte les requêtes pour vérifier le token
- **UserDetailsService** : Charge les utilisateurs pour l'authentification

## 🔧 Configuration

### Changer la base de données (PostgreSQL)

1. Modifier `build.gradle.kts` :
```kotlin
// Remplacer
runtimeOnly("com.h2database:h2")

// Par
runtimeOnly("org.postgresql:postgresql")
```

2. Modifier `application.yml` :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/votre_db
    username: votre_username
    password: votre_password
  jpa:
    hibernate:
      ddl-auto: update  # Utilise 'update' au lieu de 'create-drop'
```

### Changer la clé secrète JWT

Dans `application.yml`, modifiez :
```yaml
jwt:
  secret: VotreNouvelleClefSecreteTresLongue123456789
  expiration: 86400000  # 24 heures
```

⚠️ **Important** : En production, utilisez une variable d'environnement !

## 📝 Tests avec cURL ou Postman

### Exemple de workflow complet

```bash
# 1. S'inscrire
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","username":"Test User","password":"password123"}'

# Copier le token de la réponse

# 2. Créer une tâche
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -d '{"title":"Ma première tâche","description":"Description","completed":false}'

# 3. Lister les tâches
curl -X GET http://localhost:8080/api/todos \
  -H "Authorization: Bearer VOTRE_TOKEN"

# 4. Marquer comme complétée
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -d '{"title":"Ma première tâche","description":"Description","completed":true}'
```

## 🐛 Débogage

### Problèmes courants

1. **Port 8080 déjà utilisé**
   - Modifiez le port dans `application.yml` : `server.port: 8081`

2. **Token invalide / expiré**
   - Reconnectez-vous pour obtenir un nouveau token

3. **Erreur 403 Forbidden**
   - Vérifiez que le token est bien envoyé dans l'en-tête `Authorization`
   - Vérifiez que vous avez les droits nécessaires (rôle ADMIN si requis)

## 📚 Pour aller plus loin

### Fonctionnalités à ajouter

- Pagination des résultats
- Filtres et recherche avancée
- Upload de fichiers
- Envoi d'emails
- WebSockets pour temps réel
- Tests unitaires et d'intégration
- Documentation OpenAPI/Swagger
- Docker et Docker Compose
- CI/CD (GitHub Actions, GitLab CI)

### Ressources utiles

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [Documentation Kotlin](https://kotlinlang.org/docs/home.html)
- [Guide Spring Security](https://spring.io/guides/topicals/spring-security-architecture)
- [JWT.io](https://jwt.io/) - Décodeur de tokens JWT

## 📄 Licence

Ce projet est créé à des fins éducatives. Libre d'utilisation et de modification.

---

**Bon apprentissage ! 🚀**