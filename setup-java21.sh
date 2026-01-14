#!/bin/bash

# Script pour installer et configurer Java 21

echo "🔧 Installation de Java 21..."

# Installer Java 21 via apt
sudo apt update
sudo apt install -y openjdk-21-jdk

# Configurer Java 21 par défaut
sudo update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java
sudo update-alternatives --set javac /usr/lib/jvm/java-21-openjdk-amd64/bin/javac

# Vérifier l'installation
echo ""
echo "✅ Version Java installée:"
java -version

echo ""
echo "🚀 Vous pouvez maintenant lancer l'application avec:"
echo "./gradlew clean bootRun"
