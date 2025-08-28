# Spring Security Minimal JWT

Une application Spring Boot démontrant l'implémentation d'une authentification JWT avec Spring Security.

## Technologies utilisées

- **Spring Boot 3.5.5**
- **Spring Security**
- **JWT (JSON Web Token)** avec jjwt
- **Spring Data JPA**
- **Base de données H2** (en mémoire)
- **Lombok**
- **Maven**

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+ (ou utiliser le wrapper Maven inclus)

## Installation et lancement

### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd spring-security-minimal-jwt
```

### 2. Lancer l'application
```bash
# Avec Maven wrapper (recommandé)
./mvnw spring-boot:run

# Ou avec Maven installé
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

## Configuration

### Base de données H2
- **URL**: `jdbc:h2:mem:testdb`
- **Console H2**: `http://localhost:8080/h2-console`
- **Username**: `root`
- **Password**: (vide)

### JWT
- **Secret**: Configuré dans `application.properties` (à changer en production)
- **Cookie name**: `COOKIE`

## API Endpoints

### Authentication (`/auth`)

#### Inscription
```http
POST /auth/register
Content-Type: application/json

{
  "username": "utilisateur",
  "password": "motdepasse",
  "role": "USER"  // optionnel, par défaut "USER"
}
```

#### Connexion
```http
POST /auth/login
Content-Type: application/json

{
  "username": "utilisateur",
  "password": "motdepasse"
}
```

La connexion retourne un cookie JWT qui sera utilisé pour les requêtes authentifiées.

### Endpoints de test (`/hello`)

#### Public (sans authentification)
```http
GET /hello/public
```

#### Privé (authentification requise)
```http
GET /hello/private
Cookie: COOKIE=<jwt-token>
```

#### Admin seulement (rôle ADMIN requis)
```http
GET /hello/private-admin
Cookie: COOKIE=<jwt-token>
```

### Gestion des offres d'emploi (`/jobs`)

#### Lister toutes les offres
```http
GET /jobs
```

#### Obtenir une offre par ID
```http
GET /jobs/{id}
```

#### Créer une nouvelle offre (authentification requise)
```http
POST /jobs
Cookie: COOKIE=<jwt-token>
Content-Type: application/json

{
  "title": "Titre de l'offre",
  "description": "Description de l'offre",
  "company": "Nom de l'entreprise"
}
```

#### Supprimer une offre (propriétaire ou admin)
```http
DELETE /jobs/{id}
Cookie: COOKIE=<jwt-token>
```

#### Supprimer une offre (admin seulement)
```http
DELETE /jobs/{id}/admin
Cookie: COOKIE=<jwt-token>
```

## Sécurité

### Rôles
- **USER**: Accès aux endpoints privés, peut créer et supprimer ses propres offres
- **ADMIN**: Accès complet, peut supprimer toutes les offres

### Endpoints publics
- `/auth/login`
- `/auth/register`
- `/hello/public`
- `/jobs` (GET)
- `/jobs/{id}` (GET)
- `/h2-console/**`

### Endpoints protégés
- `/hello/private` - Tous les utilisateurs authentifiés
- `/hello/private-admin` - Rôle ADMIN seulement
- `/jobs/{id}/admin` (DELETE) - Rôle ADMIN seulement

## Tests

Pour exécuter les tests :
```bash
./mvnw test
```

## Données d'initialisation

L'application initialise automatiquement deux utilisateurs au démarrage :
- **Utilisateur normal** : `user` / `password` (rôle USER)
- **Administrateur** : `admin` / `password` (rôle ADMIN)

## Exemple d'utilisation avec curl

### 1. S'inscrire
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test123", "role": "USER"}'
```

### 2. Se connecter et récupérer le cookie
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test123"}' \
  -c cookies.txt
```

### 3. Accéder à un endpoint privé
```bash
curl http://localhost:8080/hello/private \
  -b cookies.txt
```

### 4. Créer une offre d'emploi
```bash
curl -X POST http://localhost:8080/jobs \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"title": "Développeur Java", "description": "Poste de développeur", "company": "Ma Société"}'
```

## Structure du projet

```
src/main/java/com/example/demo/
├── DemoApplication.java          # Point d'entrée
├── SecurityConfig.java           # Configuration Spring Security
├── config/
│   └── DataInitializer.java      # Initialisation des données
├── controller/
│   ├── HelloController.java      # Endpoints de démonstration
│   ├── JobOfferController.java   # CRUD offres d'emploi
│   └── LoginController.java      # Authentification
├── models/
│   ├── JobOffer.java             # Entité offre d'emploi
│   └── UserApp.java              # Entité utilisateur
├── repositories/
│   ├── JobOfferRepository.java
│   └── UserAppRepository.java
└── services/
    └── JwtService.java           # Service JWT
```

## Production

⚠️ **Attention** : Cette application est destinée à la démonstration. Pour un usage en production :

1. Changez la clé secrète JWT dans `application.properties`
2. Utilisez une base de données persistante (PostgreSQL, MySQL, etc.)
3. Ajoutez une validation appropriée des données d'entrée
4. Implémentez une gestion d'erreurs plus robuste
5. Configurez HTTPS
6. Ajoutez des logs de sécurité