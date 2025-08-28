# 🚀 Job Board - Application d'Offres d'Emploi

Une application web moderne de gestion d'offres d'emploi construite avec **Spring Boot**, **Spring Security**, **JWT**, **Thymeleaf** et **Bootstrap**.

## 📋 Table des Matières

- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#️-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [Utilisation](#-utilisation)
- [API REST](#-api-rest)
- [Comptes de Test](#-comptes-de-test)
- [Architecture](#-architecture)
- [Sécurité](#️-sécurité)
- [Développement](#-développement)

## ✨ Fonctionnalités

### 👤 Pour les Utilisateurs Non-Connectés
- ✅ Consulter la liste des offres d'emploi
- ✅ Voir les détails d'une offre
- ✅ Créer un compte utilisateur
- ✅ Se connecter

### 🔐 Pour les Utilisateurs Connectés
- ✅ Publier une nouvelle offre d'emploi
- ✅ Supprimer ses propres offres
- ✅ Accès à toutes les fonctionnalités publiques

### 👑 Pour les Administrateurs
- ✅ Supprimer n'importe quelle offre d'emploi
- ✅ Accès complet à toutes les fonctionnalités

## 🛠️ Technologies

- **Backend :** Spring Boot 3.5.5
- **Sécurité :** Spring Security + JWT
- **Base de données :** H2 (en mémoire)
- **ORM :** Spring Data JPA / Hibernate
- **Frontend :** Thymeleaf + Bootstrap 5
- **Cryptage :** BCrypt
- **Build :** Maven

## 📋 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8+** 
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)
- **Navigateur web** moderne

## 🔧 Installation

### 1. Cloner le Repository

```bash
git clone <repository-url>
cd spring-security-minimal-jwt
```

### 2. Construire le Projet

```bash
mvn clean install
```

### 3. Lancer l'Application

```bash
mvn spring-boot:run
```

### 4. Accéder à l'Application

🌐 **Interface Web :** [http://localhost:8081](http://localhost:8081)

📊 **Console H2 :** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
- **JDBC URL :** `jdbc:h2:mem:testdb`
- **Username :** `root`
- **Password :** *(vide)*

## ⚙️ Configuration

### Fichier `application.properties`

```properties
# Port du serveur
server.port=8081

# Base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=root
spring.datasource.password=
spring.h2.console.enabled=true

# JWT Configuration
jwt.secret=svbfdsbfdfdbdfbfdbfdbfdbdfbdfbfdfdbdfbdffddsfvsdsdcvdsv
jwt.cookie_name=COOKIE

# Thymeleaf
spring.thymeleaf.cache=false
```

### Variables Importantes

| Variable | Description | Valeur par Défaut |
|----------|-------------|-------------------|
| `server.port` | Port de l'application | `8081` |
| `jwt.secret` | Clé secrète JWT | `svbfdsbf...` |
| `jwt.cookie_name` | Nom du cookie JWT | `COOKIE` |

## 🎯 Utilisation

### 🌐 Interface Web

1. **Accueil :** [http://localhost:8081](http://localhost:8081)
   - Voir la liste des offres
   - Statistiques (nombre d'offres, entreprises, villes)

2. **Inscription :** Cliquez sur "Inscription"
   - Choisissez votre rôle (USER ou ADMIN)
   - Créez votre compte

3. **Connexion :** Cliquez sur "Connexion"
   - Utilisez vos identifiants
   - Ou utilisez les comptes de test

4. **Gestion des Offres :**
   - **Créer :** Bouton "Créer une offre" (connecté)
   - **Voir détails :** Cliquez sur "Voir détails"
   - **Supprimer :** Bouton "Supprimer" (propriétaire ou admin)

### 📱 API REST

Base URL : `http://localhost:8081`

#### Authentification

```bash
# Connexion
POST /auth/login
Content-Type: application/json

{
    "username": "john",
    "password": "password123"
}

# Inscription
POST /auth/register
Content-Type: application/json

{
    "username": "nouveau_user",
    "password": "motdepasse123",
    "role": "USER"
}

# Déconnexion
POST /auth/logout
```

#### Gestion des Offres

```bash
# Lister toutes les offres
GET /jobs

# Voir une offre spécifique
GET /jobs/1

# Créer une offre (authentifié)
POST /jobs
Content-Type: application/json

{
    "title": "Développeur Java",
    "description": "Nous recherchons un développeur Java expérimenté...",
    "company": "TechCorp",
    "location": "Paris",
    "salary": 50000
}

# Supprimer une offre (propriétaire ou admin)
DELETE /jobs/1

# Supprimer une offre (admin uniquement)
DELETE /jobs/1/admin
```

## 👥 Comptes de Test

L'application génère automatiquement des comptes de test au démarrage :

| Username | Password | Rôle | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | **ADMIN** | Administrateur complet |
| `john` | `password123` | **USER** | Utilisateur standard |
| `marie` | `password123` | **USER** | Utilisateur standard |
| `paul` | `password123` | **USER** | Utilisateur standard |

### 🎯 Données de Test

**6 offres d'emploi** sont automatiquement créées :
1. Développeur Java Senior (TechCorp - Paris - 65k€)
2. Data Scientist (DataSoft - Lyon - 55k€)
3. Chef de Projet IT (InnovaTech - Marseille - 70k€)
4. Développeur Frontend React (WebAgency - Toulouse - 45k€)
5. DevOps Engineer (CloudFirst - Nantes - 60k€)
6. Analyste Cybersécurité (SecureIT - Bordeaux - 58k€)

## 🏗️ Architecture

### Structure du Projet

```
src/main/java/com/example/demo/
├── config/
│   └── DataInitializer.java          # Données de test
├── controller/
│   ├── HelloController.java          # Endpoints de test
│   ├── JobOfferController.java       # API REST offres
│   ├── LoginController.java          # API REST auth
│   └── WebController.java            # Pages web
├── models/
│   ├── JobOffer.java                 # Entité offre d'emploi
│   └── UserApp.java                  # Entité utilisateur
├── repositories/
│   ├── JobOfferRepository.java       # Repository offres
│   └── UserAppRepository.java        # Repository utilisateurs
├── services/
│   └── JwtService.java               # Service JWT + Filtre
└── SecurityConfig.java               # Configuration sécurité
```

### Frontend

```
src/main/resources/
├── static/css/
│   └── style.css                     # Styles personnalisés
└── templates/
    ├── create-job.html               # Création d'offre
    ├── home.html                     # Page d'accueil
    ├── job-detail.html               # Détail offre
    ├── layout.html                   # Template de base
    ├── login.html                    # Connexion
    └── register.html                 # Inscription
```

## 🛡️ Sécurité

### Authentification JWT

- **Cookies httpOnly** pour stocker les tokens
- **BCrypt** pour le hashage des mots de passe
- **Durée de validité :** 5 heures
- **Expiration automatique** des tokens

### Contrôle d'Accès

| Endpoint | Public | USER | ADMIN |
|----------|--------|------|-------|
| `GET /` | ✅ | ✅ | ✅ |
| `GET /jobs` | ✅ | ✅ | ✅ |
| `GET /job/*` | ✅ | ✅ | ✅ |
| `POST /jobs` | ❌ | ✅ | ✅ |
| `DELETE /jobs/{id}` | ❌ | ✅* | ✅ |
| `DELETE /jobs/{id}/admin` | ❌ | ❌ | ✅ |
| `GET /create-job` | ❌ | ✅ | ✅ |

*\* Uniquement ses propres offres*

### Protection CSRF

- **Désactivée** pour l'API REST
- **Tokens CSRF** pour les formulaires web
- **SameSite cookies** pour la protection

## 💻 Développement

### Lancer en Mode Développement

```bash
# Avec rechargement automatique
mvn spring-boot:run

# Ou avec profil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Tests

```bash
# Exécuter les tests
mvn test

# Tests avec couverture
mvn test jacoco:report
```

### Logs de Debug

Pour activer les logs détaillés, modifiez `application.properties` :

```properties
logging.level.com.example.demo=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Personnalisation

#### Changer le Port

```properties
server.port=8080
```

#### Modifier la Durée JWT

Dans `JwtService.java` :

```java
public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24 heures
```

#### Ajouter de Nouveaux Rôles

1. Modifier `UserApp.java` pour ajouter des rôles
2. Mettre à jour `SecurityConfig.java`
3. Adapter les templates Thymeleaf

## 🐛 Dépannage

### Problèmes Courants

**Port déjà utilisé :**
```bash
Error: Port 8081 is already in use
```
Solution : Changer le port dans `application.properties`

**JWT Cookie non supprimé :**
- Vider le cache du navigateur
- Vérifier les DevTools → Application → Cookies

**Erreur 403 Forbidden :**
- Vérifier les rôles utilisateur
- Contrôler la configuration de sécurité

### Base de Données

**Console H2 inaccessible :**
```properties
spring.h2.console.enabled=true
```

**Données perdues au redémarrage :**
C'est normal, H2 est en mémoire. Pour persister :
```properties
spring.datasource.url=jdbc:h2:file:./data/testdb
```

## 📚 Ressources

- [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - Décodeur JWT
- [Bootstrap Documentation](https://getbootstrap.com/docs/5.1/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changes (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

## 🚀 Démarrage Rapide

```bash
# 1. Cloner et installer
git clone <repository-url>
cd spring-security-minimal-jwt
mvn clean install

# 2. Lancer
mvn spring-boot:run

# 3. Ouvrir le navigateur
# http://localhost:8081

# 4. Se connecter avec
# Username: admin
# Password: admin123
```

**🎉 C'est parti ! Votre Job Board est prêt à l'emploi !**