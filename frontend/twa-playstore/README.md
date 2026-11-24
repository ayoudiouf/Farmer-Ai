# Déploiement Android Play Store (TWA / Bubblewrap)

Ce guide permet d'empaqueter l'application web FarmerAI en une application native Android (`.aab` et `.apk`) publiable sur le **Google Play Store**, sans réécrire le code en Flutter ou React Native.

---

## 1. Prérequis
* Node.js (v18+)
* Java JDK 17 ou 21
* Android SDK (si besoin, Bubblewrap peut l'installer automatiquement)
* Le frontend déployé en HTTPS (ex: `https://farmerai.tech`)

---

## 2. Étapes de génération

### Étape 1 : Installer Bubblewrap CLI
```bash
npm install -g @bubblewrap/cli
```

### Étape 2 : Initialiser le projet TWA
Depuis le dossier `twa-playstore/` :
```bash
bubblewrap init --manifest https://farmerai.tech/manifest.webmanifest
```

Lors de l'initialisation, Bubblewrap posera quelques questions simples :
* **Package ID :** `sn.farmerai.app`
* **App Name :** `FarmerAI`
* **Short Name :** `FarmerAI`
* **Display mode :** `standalone`
* **Keystore :** Générez une nouvelle clé (notez bien le mot de passe et sauvegardez le fichier `.keystore`)

### Étape 3 : Compiler le bundle Android (.aab)
```bash
bubblewrap build
```
Cette commande génère :
* `app-release-bundle.aab` : le fichier prêt à être uploadé sur la console **Google Play**.
* `app-release-signed.apk` : pour tester directement sur un téléphone Android avec `adb install app-release-signed.apk`.

---

## 3. Digital Asset Links (Indispensable pour masquer la barre URL de Chrome)
Google exige de prouver que vous possédez le nom de domaine :
1. Récupérez la signature SHA-256 de votre keystore (affichée par Bubblewrap).
2. Déposez le fichier `.well-known/assetlinks.json` à la racine de votre serveur web (`https://farmerai.tech/.well-known/assetlinks.json`) :
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "sn.farmerai.app",
      "sha256_cert_fingerprints": [
        "VOTRE_FINGERPRINT_SHA256_FOURNI_PAR_BUBBLEWRAP"
      ]
    }
  }
]
```
Une fois ce fichier en place, l'application s'ouvre en **plein écran absolu** comme une véritable application native !
