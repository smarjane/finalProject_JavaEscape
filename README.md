# JavaEscape – README du projet


## 1. Présentation du projet
JavaEscape est une application JavaFX composée de plusieurs mini jeux et scènes narratives.
Le joueur progresse à travers différentes étapes : un menu, un dialogue d’introduction, un quiz basé sur une API externe, un dialogue intermédiaire, un jeu de Mastermind, puis un dialogue de victoire ou de défaite selon les résultats.


## 2. Tutoriel d’installation
### 2.1. Installation du projet
- Cloner le projet depuis GitHub.
- Ouvrir le projet dans IntelliJ.
- Vérifier que Maven a bien chargé les dépendances. (important sinon on oeut rencontrer des problème lors du Run)
### 2.2. Installation de la dépendance JSON (nécessaire pour l’API du quiz)
Dans le fichier pom.xml, ajouter :
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20210307</version>
</dependency>
### 2.3. Synchronisation Maven
Dans IntelliJ :
- Ouvrir l’onglet Maven à droite
- Cliquer sur l’icône des flèches tourantes « Sync All Maven Projects »
### 2.4. Mise à jour du module-info.java
Ajouter la ligne suivante :
requires org.json;
Cette étape est indispensable pour permettre au module JavaFX d’accéder à la librairie JSON. Si elle n’est pas ajouter, tous les imports dans le quizzController apparaitrons en rouge 
### 2.5. Rebuild et lancement
- Menu Build → Rebuild Project
- Lancer l’application via la classe principale


## 3. Guide de l’application et hiérarchie des scènes
L’application suit un chemin précis :
- Menu principal
- Dialogue de début
- Quiz (20 questions, victoire à 5 bonnes réponses) 
    - Si le joueur perd : Dialogue de fin de défaite
    - Si le joueur gagne : Dialogue intermédiaire
- Mastermind 
    - Si le joueur perd : Dialogue de défaite
    - Si le joueur gagne : Dialogue de victoire
- Depuis les dialogues finaux : possibilité de revenir au menu via un bouton « revenir au menu »


## 4. À savoir
### 4.1. Branche principale
La branche principale du projet est Master et non main.
### 4.2. Caractères HTML dans les questions du quiz
L’API OpenTDB renvoie parfois des caractères HTML encodés.
Une fonction decodeHtml() a été ajoutée pour corriger la majorité des cas.
Cependant, plusieurs sources indiquent que certains caractères peuvent encore apparaître même après avoir mis des fonctions spécifique à ce problème selon les questions comme celle-ci http - Opentdb weird characters - Stack Overflow de Stack Overflow
### 4.3. Problème de commit/push des images
IntelliJ ne prenait pas en compte certains fichiers .jpg ou .png lors des commits via l’interface graphique.
Solution utilisée :
- Passage par le terminal Git
- Ajout manuel des fichiers (git add .)
- Commit et push
- Les images sont désormais correctement présentes sur Github

### 4.4. Autre
- Par manque de temps sur cette dernière journée, nous n'avons pas pû terminer le CSS ainsi que de mieux répartir les fonctions en plusieurs fichiers.

## 5. Utilisation de l’IA dans le projet pour MARJANE 
Avant d’utiliser l’IA, des recherches personnelles ont été effectuées, mais certaines réponses n’ont pas été trouvées.
L’IA a été utilisée de manière respectueuse :
- Aide pour importer la dépendance JSON dans Maven
- Aide pour résoudre des erreurs de module (requires org.json)
- Aide pour structurer le code de navigation entre les pages
- Aide pour corriger des erreurs JavaFX
- Aide pour intégrer le background du début 
- Les parties générées par IA sont clairement commentées dans le code
- Aide pour les parties de style pour les fichiers .fxml et css notamment le quizz.fxml


L’objectif était de l’utiliser l’IA comme assistant technique et pas de me remplacer !

Pour Thibaud :
- Aide pour le debug et la compréhension des erreurs IntelliJ
- Aide pour le debug d'un problème lié au saves sur le MasterMind