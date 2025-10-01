FROM openjdk:17.0.2-jdk

LABEL author="Fab.16" \
      version="1.0.0" \
      description="Image de la version Java de SOM"

# Définition du répertoire de travail
WORKDIR /app

# Copie des dossiers 'code' et 'data'
COPY code/ ./code/
COPY data/ ./data/

# Compiler les fichiers Java et placer les fichiers .class dans le répertoire 'bin'
RUN mkdir bin && javac -d bin code/*.java

# Exécution de l'application
CMD ["java", "-cp", "bin", "code.Lancement"]