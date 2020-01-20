## Batterie de tests 

### Tests unicité du pseudo 
#### Lorsqu'un utilisateur a déjà pris le pseudo
- Installer et lancer SuperClavardeur™ sur deux terminaux 
- S'identifier avec un pseudo (non vide) avec un terminal
- Remplir le champs pseudo de *Vue Choix Pseudo* avec le même pseudo
→ Un message d'erreur s'affiche : `Ton pseudo est déjà pris désolé :'( Dommage... 🙈`

#### Lorsque deux utilisateurs veulent le même pseudo en même temps
- Installer et lancer SuperClavardeur™ sur deux terminaux 
- Rentrer un même pseudo (non vide) dans le champs pseudo de *Vue choix pseudo*
- Cliquer sur `s'identifier`, simultanément sur les deux machines
→ Un message d'erreur s'affiche : `Ton pseudo est déjà pris désolé :'( Dommage... 🙈`

### Tests réseau
#### Localhost
- Sélectioner son propre pseudo dans la `zone de découverte` de la *Vue principale*
- Envoyer un message
→ La reception est instantanée : vous pouvez désormais vous écrire des penses-bêtes !

#### Local
- Sélectioner un pseudo connecté (vert) sur le réseau local dans la `zone de découverte` de la *Vue principale*
- Envoyer un message 
→ Le destinataire le reçoit
→ Le message sera désormais consultable à tout moment dans la `visualisation hitorique` associé à ce pseudo destinataire !

#### Internet
- Sélectioner un pseudo connecté (vert) sur un réseau distant dans la `zone de découverte` de la *Vue principale*.
- Envoyer un message
→ Le destinataire le reçoit 
→ Le message sera désormais consultable à tout moment dans la `visualisation hitorique` associé à ce pseudo destinataire !

<br><br><br>
[< Choix d'implémentation](choix.md)
retour au [sommaire](README.md)<br>