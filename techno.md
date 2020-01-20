## Technologie 

### Java SE 11
Java est un langage de programmation orienté objet.
[télécharger](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html "oracle.com/downloads")

### ant
Ant (fondation Apache) est un logiciel qui automatise la compilation et la génération de la documentation de projets Java.<br>
Ant permet de gagner du temps<br>
Compiler l'ensemble du projet :
```ant```<br>
Générer la javadoc à retrouver dans doc/ : 
```ant javadoc```<br>
Ant permet également de d'archiver au format JAR ou WAR.<br>

### ini4j
ini4j est une API Java simple pour gérer les fichiers de configuration au format .ini de Windows.<br>
voir [`config.ini`](config.ini)

### tcp hole punching
Utile pour connecter deux hôtes d'un réseau privé naté qui essaient de se connecter l'un à l'autre avec des connexions TCP sortantes.

### serveur HTTP
HTTP (protocole de transfert hypertexte) est un protocole de la couche application. 
Nous l'utilisons ici pour sa fiabilité (utilise TCP), sa large utilisation (donc pare-feu cléments) et pour la simplicité d'utilisation de ces primitives.

### Design Pattern 
#### Singleton
- BD
- Reseau

#### PropertyChangeListener
- Thread Serveur TCP → Serveur TCP → Reseau → Controlleur Application
- Serveur UDP → Reseau → Controlleur Application

#### Serialization 
- Message
- Personne

#### Factory
- Message


<br><br><br>
[< Manuel d'utilisation](manuel.md)•[Choix d'implémentation >](choix.md)<br>
retour au [sommaire](README.md)<br>