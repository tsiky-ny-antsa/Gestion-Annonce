#   FFR Backend: application

# Technologie: JavaFX, maven, mysql mot de passe root:if(tf/0==pi)Mkj() 

## Architecture des données

### **Tables principales :**

1. **User** - Utilisateurs du système
2. **Category** - Catégories d'annonces
3. **Annonce** - Annonces à diffuser
4. **Programme** - Programmation des annonces

### **Flux de données :**

```
Annonce → Programme 
```

### **Règles de gestion :**

#### **Utilisateurs :**

- Seuls les utilisateurs `root` peuvent ajouter/supprimer des utilisateurs
- Seuls les utilisateurs `root` peuvent gérer les catégories
- Tous les utilisateurs peuvent créer/modifier des annonces et programmes

#### **Programmation :**

- Une **annonce** a un `nbrPrev` (nombre de diffusions prévues)
- Un **programme** définit une date avec dif1/dif2/dif3 (3 créneaux par date)
- Une fois une annonce créer, l'user est redirigé vers une foramulaire de programme associé à cette date, il peut ajouter plusieru programme pour une annonce (differente date)

#### **Nettoyage automatique :**

- Fichiers audio, programmes : supprimés après 15 jours
- Annonces et diffusions : conservées comme archives

### **Fonctionnalités :**

1. **Gestion des annonces** - CRUD complet
2. **Programmation** - Planifier les diffusions par date/créneaux
4. **Playlists** - Créer des playlists à partir du programme d'aujourdhui pour lire sur l'application

### **Interface utilisateur :**

- **Thème vert** (végétation)
- **Navigation par onglets** : Catégories → Annonces → Programmes → Diffusions → Playlists
- **Workflow intuitif** : Création annonce → Programmation (obligatoire)
- **Gestion manuelle** des créneaux dif1/dif2/dif3
- Page: 
  - annonce: liste des annoces: lorsque on clique sur une annoce on est rediriger vers la programme de cette annoce. On peut modifier l'annonce, le fichier audio, le nombre de diffusion, 
  - page programme: Calendrier; lorsqu'on clique sur une date on est rediriger vers la programme de cette date
  - on -air: lecture du programme d'aujourdhuir: regrouper par: dif 1 2 3
  - Catecorie(root): ajouter des nouvelles catégorie
  - Gestion(root): ajout user, supprimer user


