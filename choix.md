
## Choix d'implémentation
### Choix technologiques
#### ant
Ant (fondation Apache) est un logiciel qui automatise la compilation et la génération de la documentation de projets Java.<br>
Ant permet de gagner du temps<br>
Compiler l'ensemble du projet :
```ant```<br>
Générer la javadoc à retrouver dans doc/ : 
```ant javadoc```<br>
Ant permet également de d'archiver au format JAR ou WAR.<br>
#### ini4j
ini4j est une API Java simple pour gérer les fichiers de configuration au format .ini de Windows.<br>
voir [`config.ini`](config.ini)

#### tcp hole punching
Utile pour connecter deux hôtes d'un réseau privé naté essaient de se connecter l'un à l'autre avec des connexions TCP sortantes.

### Choix de conception
#### MVC
`<diagramm class>`
#### Design Pattern utilisés
##### Singleton
- BD
- Reseau

##### PropertyChangeListener
- Thread Serveur TCP → Serveur TCP → Reseau → Controlleur Application
- Serveur UDP → Reseau → Controlleur Application

##### Serialization 
- Message
- Personne

##### Factory
- Message



[< manuel d'utilisation](manuel.md "< manuel")
retour au [sommaire](README.md)<br>