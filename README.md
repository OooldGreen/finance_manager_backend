# Introduction

Ce projet consiste à concevoir et développer un système de gestion des finances personnelles. L'objectif est de permettre un pilotage précis des revenus et des dépenses (actuels et futurs), la gestion des budgets, ainsi que l'organisation de notes financières via un système de tags.

# Fonctionnalités Principales

- Gestion des Utilisateurs : Inscription, connexion, mise à jour du profil et modification du mot de passe (Sécurisé via Spring Security).
- Suivi Multi-comptes : Pilotage et suivi des soldes selon différents modes (Espèces, Compte courant, Épargne).
- Gestion des Flux (Revenus/Dépenses) : Suivi en temps réel des flux financiers actuels et planification des flux futurs.
- Objectifs Financiers : Définition de buts financiers et suivi de leur état d'avancement.
- Pilotage Budgétaire : Définition de budgets mensuels avec calcul automatique en fin de mois (via Spring Scheduler et expressions Cron).
- Prise de Notes : Création de notes financières pour référence ultérieure.
- Système de Tags : Attribution de tags aux transactions et aux notes pour faciliter la recherche et le filtrage.

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
![Physical data model.png](../Physical%20data%20model.png)

# Roadmap

| **Phase** | **Durée Estimée** | **Missions Principales** |
| --- | --- | --- |
| **Configuration & Modélisation** | 20h | Mise en place de l'environnement Maven, config BDD, conception des entités core (User, Budget, Category, etc.). |
| **Logique Métier (Backend)** | 40h | Développement des couches Controller, Service et Repository. Implémentation des calculs (alertes de dépassement, agrégation). |
| **Sécurité & JWT** | 15h | Intégration de Spring Security et gestion des tokens JWT pour la sécurisation des endpoints.
| **Tests & Débogage (Postman)** | 10h | Validation des API REST, gestion des exceptions et tests d'intégration. |
| **Interface Frontend (React)** | 15h | Création d'un Dashboard simple avec React pour la visualisation des données via API. |