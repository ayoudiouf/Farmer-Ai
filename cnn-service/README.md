# FarmerAI CNN Service

Microservice Python (FastAPI + PyTorch) de détection de maladies végétales par photo.
Appelé par le backend Spring Boot (`CnnDiagnosticClient`) sur la route `/predict`.

## Démarrage

```bash
cd cnn-service
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

Vérifier que le service tourne : `GET http://localhost:8000/health`
→ `{"status": "ok", "modele_charge": false}` tant qu'aucun modèle n'est entraîné.

## Entraîner le modèle

Le service répond honnêtement "modèle non entraîné" tant qu'aucun `checkpoint.pt`
n'existe — c'est volontaire, pour ne jamais donner un faux diagnostic à un agriculteur.

### Workflow de collecte (pendant le pilote, 3 coopératives)

1. **Créer l'arborescence du dataset** :
   ```bash
   python scripts/build_dataset_structure.py --output dataset_brut
   ```
   Crée un dossier par classe de maladie (voir `app/model/labels.py`).

2. **Collecter les photos sur le terrain.** Deux options :
   - Manuelle : dépose les photos (déjà validées/identifiées par un agronome)
     directement dans le bon dossier `dataset_brut/<classe>/`.
   - Via l'app : les agents terrain envoient les photos avec l'endpoint
     `POST /dataset/contribuer` (champs `file`, `classe`, `agent_id`) — utile
     pour une collecte décentralisée sans accès direct au serveur.
     `GET /dataset/classes` renvoie la liste des classes pour peupler un menu.

3. **Valider le dataset avant d'entraîner** :
   ```bash
   python scripts/validate_dataset.py --data-dir dataset_brut
   ```
   Vérifie le nombre de photos par classe (min. recommandé : 50), détecte
   les fichiers corrompus et les classes vides.

4. **Séparer train/validation** :
   ```bash
   python scripts/split_dataset.py --input dataset_brut --output dataset_split
   ```

5. **Lancer l'entraînement** :
   ```bash
   python -m app.train --data-dir dataset_split/train --epochs 15
   ```
   Le fichier `app/model/checkpoint.pt` est généré — le service le charge
   automatiquement au prochain redémarrage.

### Complément avec des données publiques

Le dataset PlantVillage (public, sur Kaggle) couvre plusieurs des maladies
listées dans `labels.py` et peut servir à pré-entraîner le modèle avant de
l'affiner avec tes photos de terrain sénégalaises (plus représentatives des
conditions réelles). Mélange les deux sources dans `dataset_brut/` avant de
lancer la validation et le split.

## Déploiement

Ce service tourne séparément du backend Spring Boot (autre port, éventuellement
autre serveur si besoin de GPU). En production, définir `app.cnn-service.url`
côté Spring Boot vers l'adresse réelle de ce service (ex. conteneur Docker
`http://cnn-service:8000`, ou VM dédiée).
