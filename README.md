# FarmerAI — Structure du projet

Architecture sur le modèle SmartMifin : **Spring Boot** (backend) + **Angular** (frontend), JWT, PostgreSQL, Hibernate.

## Structure

```
farmerai/
├── backend/     # API Spring Boot
│   └── src/main/java/sn/farmerai/
│       ├── config/       # Sécurité, JWT
│       ├── controller/   # Endpoints REST (auth, diagnostics)
│       ├── model/        # Entités JPA (User, Diagnostic)
│       ├── repository/   # Repositories Spring Data
│       └── dto/          # Objets de transfert
└── frontend/    # App Angular (standalone components)
    └── src/app/
        ├── core/         # Services, guards, intercepteurs
        └── features/     # auth (login), diagnostic
```

## Démarrage rapide

### Backend
```bash
cd backend
# créer la base PostgreSQL "farmerai_db" au préalable
mvn spring-boot:run
```
API disponible sur `http://localhost:8080/api`

### Frontend
```bash
cd frontend
npm install
ng serve
```
App disponible sur `http://localhost:4200`

## Ce qui est déjà en place
- Authentification JWT complète (register/login + `JwtAuthFilter` qui valide le token sur chaque requête protégée)
- Entité `User` (avec langue locale, région, rôle)
- Diagnostic photo **connecté au microservice CNN réel** (`cnn-service/`) + **stockage local durable des photos** (`PhotoStorageService`)
- **Module RAG conseils agronomiques** : fiches INERA/FAO en JSON (`resources/fiches-agronomiques/`), recherche par mots-clés (`RetrievalService`), génération de réponse via l'API Claude (`RagConseilService`) — endpoint `/api/conseils/demander`
- **Canal USSD** (`UssdController`) : menu *123# via webhook Africa's Talking, branché sur le même moteur RAG
- Dashboard Angular : historique des diagnostics + chat conseil agronomique
- Intercepteur JWT côté Angular + guard de route

## Ce qu'il reste à faire
1. **Entraîner le modèle CNN** avec un vrai dataset (voir `cnn-service/README.md`) — seule étape bloquante pour des diagnostics fiables
2. Enrichir les fiches agronomiques (actuellement 3 fiches d'exemple : mil, arachide, tomate — à étoffer avec le vrai corpus INERA/FAO)
3. Définir `ANTHROPIC_API_KEY` en variable d'environnement (jamais en dur dans le code) pour activer le module conseils
4. Traduction wolof/diola/serere (mentionnée dans le pitch, pas encore branchée dans le RAG)
5. Passer le stockage photo en S3 (ou équivalent) quand le volume grandit (An 2, 5000+ utilisateurs) — un seul fichier à modifier (`PhotoStorageService`)
6. Intégration WhatsApp Business API (le canal USSD est prêt ; WhatsApp reste à faire sur le même modèle)

## Déploiement sur le Play Store (chemin recommandé : PWA + TWA)

Angular ne se publie pas nativement sur un store — c'est une app web. Le chemin le plus rapide :

1. Transformer l'app Angular en **PWA** (`ng add @angular/pwa`) → génère le `manifest.webmanifest` et le service worker.
2. Héberger le frontend en HTTPS (obligatoire pour une PWA).
3. Utiliser **Bubblewrap** (outil officiel Google) pour empaqueter la PWA en `.aab` (Android App Bundle) :
   ```bash
   npm i -g @bubblewrap/cli
   bubblewrap init --manifest https://ton-domaine.sn/manifest.webmanifest
   bubblewrap build
   ```
4. Créer un compte développeur Google Play (25$, paiement unique) et publier le `.aab` généré.

Cette approche te permet de garder un seul code Angular pour le web ET l'app Android, sans réécrire en Flutter/React Native.
