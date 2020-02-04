## Batterie de tests 

### Tests découverte utilisateur 
- Installer et lancer SuperClavardeur™ sur deux terminaux en réseau local ou à distance
- S'identifier avec un pseudo (non vide) avec un terminal
→ l'autre utilisateur ne s'affiche pas (ou en deconnecté s'il était déjà présent sur le réseau/on lui a déjà parlé auparavant)
- S'identifier avec un pseudo (non vide) avec l'autre terminal 
→ l'autre utilisateur s'affiche en connecté

### Tests unicité du pseudo 
- Installer et lancer SuperClavardeur™ sur deux terminaux en réseau local ou à distance

#### Lorsqu'un utilisateur a déjà pris le pseudo
- S'identifier avec un pseudo (non vide) avec un terminal
- Remplir le champs pseudo de *Vue Choix Pseudo* avec le même pseudo
→ Un message d'erreur s'affiche : `Ton pseudo est déjà pris désolé :'( Dommage... 🙈`

#### Lorsque deux utilisateurs veulent le même pseudo en même temps
- Rentrer un même pseudo (non vide) dans le champs pseudo de *Vue choix pseudo*
- Cliquer sur `s'identifier`, simultanément sur les deux machines
→ Un message d'erreur s'affiche : `Ton pseudo est déjà pris désolé :'( Dommage... 🙈`

### Tests envoi/réception réseau
- Installer et lancer SuperClavardeur™ sur deux terminaux

#### Localhost
- Sélectioner son propre pseudo dans la `zone de découverte` de la *Vue principale*
- Envoyer un message/fichier
→ vous pouvez désormais vous écrire des penses-bêtes !

#### Local
- Sélectioner un pseudo connecté (vert) sur le réseau local dans la `zone de découverte` de la *Vue principale*
- Envoyer un message/fichier
→ Le destinataire le reçoit
→ Le message sera désormais consultable à tout moment dans la `visualisation hitorique` associé à ce pseudo destinataire !

#### Internet 
- Sélectioner un pseudo connecté (vert) sur un réseau distant dans la `zone de découverte` de la *Vue principale*
- Envoyer un message/fichier
→ Le destinataire le reçoit 
→ Le message sera désormais consultable à tout moment dans la `visualisation hitorique` associé à ce pseudo destinataire !


### Test groupe
- Installer et lancer SuperClavardeur™ sur trois terminaux sur le réseau local ou à distance 
- Créer un nouveau groupe en sélectionnant au moins deux autres personnes (sur réseau local ou internet) dans la *Vue creation groupe*.
→ Toutes les personnes membres du groupe ont une nouvelle entrée dans la `zone de découverte`
- Envoyer un message/fichier
→ Toutes les personnes membres reçoivent le message (dans `visualisation hitorique` le message affiche le pseudo de l'emetteur)

### Tests historique
- Prérequis:  avoir eu une ou plusieurs conversations de groupe ou de personne à personne, être identifié
- dans la fenêtre principale sélectionner un utilisateur ou un groupe, la conversation que vous avez eu s'affiche (à droite les messages dont on est l'émetteur).

<br><br><br>
[< Choix d'implémentation](choix.md)<br>
retour au [sommaire](README.md)
