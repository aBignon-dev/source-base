# TP Debugger Java - JDI

## Introduction
J'ai réalisé ce TP en utilisant Java Debug Interface (JDI) pour créer un debugger en ligne de commande. Le but etait de comprendre comment fonctionne un debugger de l'interieur et de mettre en place les fonctionnalitées de base.

## Architecture du projet
J'ai choisi une architecture basée sur le pattern Command pour gerer les differentes actions du debugger:
- Une classe abstraite `JDIAbstractDebuggerCommand` qui sert de base pour toutes les commandes
- Un gestionnaire de commandes `JDICommandManager` qui stock et execute les commandes
- Des classes specifiques pour chaque commande (step, continue, etc)

## Fonctionnalitées implementées
- Execution pas à pas (step) (Commande : step)
- Step over pour passer les appels de methodes (Commande : step-over)
- Affichage de la stack trace (Commande : stack)
- Visualisation des variables temporaires (Commande : temporaries)
- Affichage de la frame courante (Commande : frame)

## Choix techniques
Pour la conception, j'ai decidé de:
- Separer clairement les responsabilitées entre les différentes classes
- Utiliser des commandes pour faciliter l'ajout de nouvelles fonctionnalitées
- Gerer proprement les evenements JDI pour eviter les bugs
- Mettre en place une interface utilisateur simple en ligne de commande

## Difficultées rencontrées
La principale difficulté a été de comprendre comment marche JDI, notament:
- La gestion des evenements qui est pas super intuitive
- Les different types de step (into, over, etc)
- La recuperation des variables locales qui necessite pas mal de code

## Conclusion
Ce projet m'a permit de mieu comprendre le fonctionement interne d'un debugger. J'ai appris beaucoup sur JDI et sur la conception d'une architecture evolutive avec le pattern Command.

## Comment executer
1. Compiler tout les fichiers .java
2. Lancer JDISimpleDebugger avec JDISimpleDebuggee comme argument
3. Utiliser les commandes disponibles (step, continue, frame, etc)