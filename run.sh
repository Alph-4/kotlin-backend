#!/bin/bash

# Script d'installation de Java 21 et lancement de l'application

echo "🔧 Configuration de Java 21 pour le projet..."

# Source SDKMAN
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Installer Java 21 si non présent
sdk install java 21.0.5-ms || echo "Java 21 déjà installé"

# Utiliser Java 21 pour ce terminal
sdk use java 21.0.5-ms

# Vérifier la version
echo ""
echo "✅ Version Java active:"
java -version

# Nettoyer et lancer l'application
echo ""
echo "🚀 Lancement de l'application..."
./gradlew clean bootRun
