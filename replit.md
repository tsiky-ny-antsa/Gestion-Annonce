# FFR Stage Application

## Description
Application JavaFX de gestion d'annonces et de programmation pour FFR Stage.

## Technologie
- JavaFX 18.0.2
- Maven
- SQLite (base de données locale)
- BCrypt pour le hachage des mots de passe

## Architecture

### Tables de la base de données
1. **users** - Utilisateurs du système (root/user)
2. **categories** - Catégories d'annonces
3. **annonces** - Annonces à diffuser
4. **programme** - Programmation des diffusions

### Règles de gestion
- Seuls les utilisateurs `root` peuvent gérer les catégories et les utilisateurs
- Tous les utilisateurs peuvent créer/modifier des annonces et programmes
- Après création d'une annonce, redirection vers la programmation
- 3 créneaux de diffusion possibles par date (dif1, dif2, dif3)

## Connexion par défaut
- **Username**: admin
- **Password**: admin

## Fonctionnalités
1. **Annonces** - CRUD complet des annonces avec fichiers audio
2. **Programmes** - Planification par date et créneaux
3. **On-Air** - Visualisation du programme du jour par créneau
4. **Catégories** (root) - Gestion des catégories
5. **Gestion** (root) - Gestion des utilisateurs

## Interface
- Thème vert (végétation)
- Navigation par onglets
- Tableaux interactifs pour toutes les données

## Structure du projet
```
src/main/java/com/ffr/
├── MainApp.java - Point d'entrée de l'application
├── models/ - Classes modèles (User, Category, Annonce, Programme)
├── dao/ - Classes d'accès aux données
├── controllers/ - Contrôleurs JavaFX (Login, Main)
└── utils/ - Utilitaires (DatabaseConnection, SessionManager)

src/main/resources/
├── css/style.css - Feuille de style avec thème vert
└── audio/ - Fichiers audio des annonces
```

## Commandes Maven
- Compilation: `mvn clean compile`
- Exécution: `mvn javafx:run`
- Package: `mvn clean package`

## Notes
- La base de données SQLite est créée automatiquement au premier lancement
- Les fichiers audio sont stockés dans src/main/resources/audio/
- Un utilisateur admin/admin est créé par défaut
