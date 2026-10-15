# Projet Sokoban+ – Java

## Membres du groupe:
- Celine BELBEY
- Samia AREZKI   


## Présentation du projet

Ce projet consiste à développer un jeu de type **Sokoban** en Java, jouable en **console**. 

Le joueur doit déplacer des blocs dans une grille afin de recouvrir tous les objectifs.

le projet est dévisé en 2 étapes : 
**etape 1** : moteur de jeu (déplacement, énergie,bombes, victoire et défaite)
**etape 2** : solveur automatique basé sur un parcour en largeur (BFS) 


L’objectif principal était d’obtenir un jeu jouable, puis un solveur correct, plutôt que d’optimiser ou refactoriser excessivement le code.


## Organisation du projet

Le projet est organisé de la manière suivante :

 * Main.java 
  Point d’entrée du programme. Gère la boucle principale, l’affichage et les commandes rentrées par l'utilisateur.

 * Game.java 
  Contient les règles du jeu : déplacements, poussée des blocs, gestion de l’énergie, bombes, conditions de victoire et de défaite.

* Grid.java
  Représentation de la grille du niveau (terrain). Elle stocke les différents types de cases. ( un tableau 2D de **TileType**)

* Player.java
  Représente le joueur : sa position, son énergie et les bombes.

* TileType.java
  Enumération des différents types de cases (mur, bloc, bonus, etc.).

*  Direction.java
  Gère les directions de déplacement (haut, bas, gauche, droite) + leurs touches claviers et les deplacement (dx,dy).

* EtatJeu.java
  Représente un état du jeu utilisé par le solveur automatique.

* Solver.java
  Implémente le solveur automatique en utilisantune recherche en largeur (BFS).

* levels/
  Dossier contenant les niveaux du jeu (format JSON ).chaque niveau est representé par son nom, l'energie du joueur au début et la grille du jeu (**level.java**)

* Loader 
  charge les fichiers Json et crée les objets level 


## Étape 1 — Moteur de jeu

### Fonctionnalités 

- Affichage du jeu en **ASCII** dans la console
- Déplacements du joueur avec les touches :
  - z : haut
  - q : gauche
  - s : bas
  - d : droite
- Poussée des blocs
- Gestion de l’énergie :
  - chaque déplacement consomme de l’énergie
  - certaines actions coûtent consomment plus d'énergie (ex : blocs lourds)
- Recharge :
  - récupération d’énergie : (+ : +5 enegrie)
  - récupération de bombes : (E : +1 bombre)
- Bombes (b <direction>) : detruisent les murs destructibles (X)
- Conditions de victoire : tous les objectifs doivent être recouverts par un bloc
- Conditions de défaite : l’énergie du joueur atteint zéro

### Choix de conception

- Le joueur (@) et les objectifs (.) ne sont pas stockés directement dans la grille :
  - ils sont gérés séparément (overlay)
  - cela simplifie la logique de la grille
- Les objectifs sont stockés dans **Game** sous forme de positions
- La classe **Grid** contient uniquement le terrain et les éléments du niveau (murs, blocs, bonus)


## Étape 2 — Solveur automatique (BFS)

### Principe général

Un solveur automatique a été ajouté afin de trouver une solution lorsqu’elle existe à l'aide d'un algorithme de **recherche en largeur (BFS)** 
Il  explore les états possibles du jeu jusqu’à atteindre une vcitoire (la plus simple possible).

### Représentation d’un état

Chaque état du jeu est représenté par la classe **EtatJeu**, qui contient :
- joueur : la position du joueur
- blocs : la liste des blocs
- energie : l’énergie restante
- bombes : le nombre de bombes
- actions : la liste des actions déjà effectuées
- grille : une copie de la grille

Cette représentation permet de comparer les états et d’éviter de revisiter plusieurs fois le même état.
Pour cela, chaque état possède une signature unique générée par la méthode signature(), ce qui permet d’éviter les boucles lors de la recherche.

### Algorithme du solveur

- Utilisation d’une **Queue** pour gérer la recherche en largeur
- Utilisation d’un **HashSet** pour stocker les états déjà explorés
- Pour chaque état  : (tant que la file n'est pas vide)
  - tester s'il est gagant
  - generer toutes les actions possibles (déplacements et bombes)
  - appliquer ces actions pour créer un nouvel état 
  - Le solveur s’arrête dès qu’une solution est trouvée

### Résultats du solveur

- Lorsque la solution est trouvée, le solveur affiche :
  - le **nombre d’états explorés**
  - la **liste des actions**  menant à la solution
- Un **rejeu automatique** de la solution est ensuite effectué dans la console, en appliquant les actions une par une au jeu.

## Conclusion 
 le projet permet de : 
 - jouer à Sokoban dans la console 
 - utiliser l'énergie, les bonus, les bombes
 - résoudre automatiquement les niveaux
