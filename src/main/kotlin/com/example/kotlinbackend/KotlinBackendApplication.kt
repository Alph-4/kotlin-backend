package com.example.kotlinbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Point d'entrée principal de l'application Spring Boot
 * 
 * @SpringBootApplication combine trois annotations importantes :
 * - @Configuration : Indique que la classe contient des beans Spring
 * - @EnableAutoConfiguration : Active la configuration automatique de Spring Boot
 * - @ComponentScan : Scanne le package pour trouver les composants Spring
 */
@SpringBootApplication
class KotlinBackendApplication

/**
 * Fonction main qui démarre l'application
 */
fun main(args: Array<String>) {
    runApplication<KotlinBackendApplication>(*args)
}
