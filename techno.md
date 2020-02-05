## Technologie 

### Java SE 11
Java est un langage de programmation orienté objet.
télécharger la version d'[Oracle](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html "oracle.com/downloads")
ou une version libre [AdoptOpenJDK 11](https://adoptopenjdk.net/installation.html?variant=openjdk11&jvmVariant=hotspot# "adoptopenjk") 

### ant
Ant (fondation Apache) est un logiciel qui automatise la compilation et la génération de la documentation de projets Java.<br>
Ant permet de gagner du temps<br>
Compiler l'ensemble du projet :
```ant```<br>
Générer la javadoc à retrouver dans doc/ : 
```ant javadoc```<br>
Ant permet également de d'archiver au format JAR (`ant jar`) ou WAR pour les fichiers du serveur (`ant war`).<br>
Le choix de Ant plutôt que Maven ou Graddle a été réalisé en connaissance de cause. Au moment de la création du script notre projet ne respectait pas les conventions maven et il aurait été coûteux de changer cela. De plus, contrairement à Graddle ou Maven, ant nécessite de spécifier plus de détails, une faiblesse apparente qui est en fait une force le rendant extrêment flexible. Pour finir, il est toujours bon de commencer l'apprentissage de création de script de compilation par un langage qui a fait ses preuves et qui permet de se familiariser avec l'ensemble du processus de compilation (plutôt que la "magie" de Maven reposant sur des conventions cachées). 

### ini4j
ini4j est une API Java simple pour gérer les fichiers de configuration au format .ini de Windows. [site](http://ini4j.sourceforge.net/index.html)<br>
voir [`config.ini`](config.ini)

### Protocole TCP
L'envoi de message entre deux utilisateurs se base sur le protocle TCP pour garantir l'arrivée des messsages à destination, grâce à  un mécanisme de reprise des pertes.

### Protocole UDP
Ce protocole sans connexion est utilisé pour l'envoi de message en broadcast sur le réseau local où les probabilités de perte de messsage sont quasi-inexistantes. Il est aussi utilisé pour répondre aux messages reçus en broadcast. Les messages transitant par UDP sont les messages de type événements (connexion, deconnexion, mise à jour du pseudo, création d'un groupe, qui est présent, je suis présent etc. )

### Protocole HTTP
HTTP (protocole de transfert hypertexte) est un protocole de la couche application. 
Nous l'utilisons ici pour sa fiabilité (utilise TCP), sa large utilisation (donc pare-feu cléments) et pour la simplicité d'utilisation de ces primitives, pour les communications avec le serveur de présence (API Java utilisée : `Servlet` du package `javax.servlet`).

### Serveur de présence
La découverte d'usagers en dehors du réseau local nécessite un point de rendez-vous pour connaître les utilisateurs connectés. A cette fin nous avons codés un serveur tomcat ([code](https://github.com/usernamedjpris/clavardage/tree/master/servlet) qui a pour mission de répondre aux requêtes des utilisateurs.
<br> Nous avions d'abord pensé réaliser un serveur de type subscribe/publish mais l'envoi de messages asynchones par le serveur se révéla difficile. En effet, cela suppose de créer une classe se chargeant de la réception des messages http venant du serveur en java vers clavardeur (embarquer un serveur tomcat dans clavardeur client semble une solution lourde, il ne semblait pas non plus évident de gérer la réception http à la main). <br>
il fallait aussi gérer la réception de requêtes de connexion initiées depuis l'extérieur (ultérieurement nous avons trouvé une façon simple de le faire avec upnp (cf abandon technologiques)). De plus, le serveur du GEI n'étant pas/plus fonctionnel nous avons dû installer notre propre serveur ce qui a ralenti le développement.
<br> Nous avons donc opté pour un choix plus simple, qui consiste à demander à intervalle régulier au serveur la liste des personnes connectées et de mettre ainsi à jour les utilisateurs et leur status (sauf les utilisateurs déjà présents sur le réseau local (information en double sinon). Les changements envoyés en broadcast sur le réseau local sont aussi envoyés au serveur.
 Cette simplification a pour inconvénient majeur d'augmenter le nombre de requêtes (actuellement une toutes les 10s, mais facilement changeable).

### HSQLDB 
HSQLDB (ou HyperSQL Database) est un système moderne et léger de gestion de base de données relationnelle écrit en Java et disponible à partir de Java 8. Nous avons utilisé la [compatibilité](http://hsqldb.org/doc/guide/compatibility-chapt.html) de HSQLDB avec MySQL avec la ligne : 
```
SET DATABASE SQL SYNTAX MYS TRUE
```
La base de donnée est relativement simple et comporte 3 tables.
![DB_class_diagram](conception/DB_class_diagram.png)

### Design Pattern 
#### Singleton
Ces classes ne sont instanciées qu'une seule fois et accessibles partout. La BD et le réseau représentant des objets uniques centraux dans l'application, il semble judicieux d'avoir utilisé ce design pattern, sans être tombé dans l'écueil d'une utilisation trop static/globale. 
- `BD`
- `Reseau`

#### PropertyChangeListener
Mécanisme d'observé/observeur permettant de remonter les événements réseaux avec très peu de dépendances au contrôleur de l'application.
- `ServeurSocketThread` → `ServeurTCP` → `Reseau` → `ControleurApplication`
- `ServeurUDP` → `Reseau` → `ControleurApplication`
- `ClientHTTP` → `Reseau` → `ControleurApplication`

#### Serialization 
Mécanisme permettant de transformer une classe en binaire pour pourvoir recréer l'objet à partir des données binaires (sauvegarde de l'objet). Utilisé pour transférer des types Message sur le réseau. Fournit une grande simplicité d'usage et assure la cohérence des données reçues (on obtient le même objet que celui envoyé), mais crée un peu de overhead sur le réseau par rapport à une implémentation "à la main" des données à envoyer ne retenant que les champs utiles du message.
- `Message`
- `Interlocuteurs`
- `Group`
- `Personne`

#### Factory
Procédé délégant la création des Message à une classe interne (appellé judicieusement "`Factory`") permettant de donner de l'information sur l'objet construit à travers le nom de la méthode employée pour le construire (en plus de la javadoc) (avant l'utilisation de ce design pattern nous avions 8 constructeurs distincts de message rendant difficile d'identifier rapidement le type de message construit et rendant la création de message plus sujettes aux erreurs)
- `Message`

#### Composite
Refactorisation tardive du code ayant pour but de gérer facilement les groupes d'utilisateurs en plus des utilisateurs. Ce design pattern permettant de masquer la complexité des groupes/la présence de Collections en ne gérant seulement des Interlocuteurs.
- `Interlocuteurs` (Interface) ; `Interlocuteurs` (Composant) ; `Group` (Composite) 

### Choix technologiques envisagés sérieusement, finalement abandonnés
#### Observers/Observable
Depuis la version 9 de JAVA, Observers/Observable est deprecated\*. Nous avons finalement opté pour l'implémentation de `PropertyChangeListener`.

\* (À l'usage Observers/Observable s'avérait limité : on ne pouvait pas spécifier le type d'événement qui s'était produit par exemple [en savoir plus](https://bugs.openjdk.java.net/browse/JDK-8154801))

#### tcp hole punching
Le [`TCP hole punching`](https://en.wikipedia.org/wiki/TCP_hole_punching) permet de connecter deux hôtes situés dans deux réseaux privés natés. <br>
Le principe consiste à identifier un hôte du réseau privé par l'adresse publique du NAT et d'un port et nécessite un serveur avec une adresse publique.
- Les clients A, B se connectent au serveur de présence. Le serveur sauvegarde (@natA, portA) et (@natB, portB) utilisé par les clients pour la connexion.
- Le serveur envoit respectivement à A et B : (@natB, portB) et (@natA, portA).
- En utilisant ce couple de valeurs, les clients A et B peuvent continuer la communication en Peer2Peer.

Le problème de cette technologie est la difficulté de son implémentation en Java. En effet, en Java il y a `Socket` et `ServerSocket`. Si on veut envoyer l'adresse sur laquelle on écoute (`ServerSocket` de `ServerTCP`) il faudrait donc créer un `Socket` avec le même port : ce qui provoque l'erreur `java.net.BindException: Address already in use`.
L'utilisation de cette technologie aurait donc nécessité de créer un socket sur le port pour le send, le fermer et ouvrir immédiatement après un socketserveur sur le port, et ce, des 2 côtés ; et aurait alourdi le code. 
Cette technologie a donc finalement été abandonnée au profit d'[`upnp`](https://en.wikipedia.org/wiki/Universal_Plug_and_Play) et sa librairie pour Java `org.bitlet.weupnp`.
<br> Une amélioration de clavardeur serait de prendre en charge d'autres techniques pour connecter deux hôtes situés dans deux réseaux privés natés.
<br> Une piste serait d'utiliser des serveurs STUN ou TURN en se servant par exemple de librairies comme [ice4j]("https://github.com/jitsi/ice4j") ou [UCE](https://github.com/htwg/UCE) pour établir la connexion.



<br><br><br>
[< Manuel d'utilisation](manuel.md)•[Choix d'implémentation >](choix.md)<br>
retour au [sommaire](README.md)<br>



