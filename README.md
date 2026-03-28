# Introduction

Ce projet consiste à concevoir et développer un système de gestion des finances personnelles. L'objectif est de permettre un pilotage précis des revenus et des dépenses (actuels et futurs), la gestion des budgets, ainsi que l'organisation de notes financières via un système de tags.

# Fonctionnalités Principales

- Gestion des Utilisateurs : Inscription, connexion, mise à jour du profil et modification du mot de passe (Sécurisé via Spring Security).
- Suivi Multi-comptes : Pilotage et suivi des soldes selon différents modes (Espèces, Compte courant, Épargne).
- Gestion des Flux (Revenus/Dépenses) : Suivi en temps réel des flux financiers actuels et planification des flux futurs.
- Objectifs Financiers : Définition de buts financiers et suivi de leur état d'avancement.
- Pilotage Budgétaire : Définition de budgets mensuels avec calcul automatique en fin de mois (via Spring Scheduler et expressions Cron).
- Système de Tags : Attribution de tags aux transactions et aux notes pour faciliter la recherche et le filtrage.
- Filtrage: Mise en place d'un système de filtrage intelligent qui offre une visibilité instantanée sur les dépenses, facilitant ainsi le suivi budgétaire et la gestion de patrimoine.

# Pile Thechnologique

- Maven
- Java
- Spring Boot 3
- Spring Security (Authentification et Autorisation basées sur JWT)
- Spring Data JPA/Hibernate
- PostgreSQL
- Open-API (Swagger-UI)
- Lombok
- Figma

# modèle de données physiques
![Physical data model.png](/Physical%20data%20model_end.png)
