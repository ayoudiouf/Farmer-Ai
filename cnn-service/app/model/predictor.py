"""
Charge le modèle CNN entraîné (checkpoint .pt) et effectue l'inférence
sur une image envoyée par le backend Spring Boot.

Tant qu'aucun modèle entraîné n'est disponible (fichier `checkpoint.pt` absent),
le service répond en mode dégradé explicite plutôt que de donner un faux résultat -
important pour ne jamais induire un agriculteur en erreur avec une fausse confiance.
"""
import io
import os

import torch
from PIL import Image
from torchvision import transforms

from app.model.architecture import PlantDiseaseCNN
from app.model.labels import CLASSES, RECOMMANDATIONS

CHECKPOINT_PATH = os.getenv("CNN_CHECKPOINT_PATH", "app/model/checkpoint.pt")
IMAGE_SIZE = 128

TRANSFORM = transforms.Compose([
    transforms.Resize((IMAGE_SIZE, IMAGE_SIZE)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])


class DiseasePredictor:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model = None
        self._load_model()

    def _load_model(self):
        if not os.path.exists(CHECKPOINT_PATH):
            # Pas encore de modèle entraîné - voir train.py pour lancer l'entraînement
            # sur un dataset structuré type PlantVillage.
            self.model = None
            return

        model = PlantDiseaseCNN(num_classes=len(CLASSES))
        model.load_state_dict(torch.load(CHECKPOINT_PATH, map_location=self.device))
        model.to(self.device)
        model.eval()
        self.model = model

    def is_ready(self) -> bool:
        return self.model is not None

    def predict(self, image_bytes: bytes, culture: str = "inconnue") -> dict:
        if self.model is None:
            return {
                "maladie": "modele_non_entraine",
                "confiance": 0.0,
                "recommandation": (
                    "Le modèle CNN n'est pas encore entraîné pour cette culture. "
                    "Lancez app/train.py avec un jeu de données étiqueté (voir README) "
                    "pour activer la détection automatique."
                ),
            }

        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        tensor = TRANSFORM(image).unsqueeze(0).to(self.device)

        with torch.no_grad():
            logits = self.model(tensor)
            probs = torch.softmax(logits, dim=1)
            confiance, index = torch.max(probs, dim=1)

        classe = CLASSES[index.item()]
        return {
            "maladie": classe,
            "confiance": round(confiance.item(), 4),
            "recommandation": RECOMMANDATIONS.get(classe, "Consultez un agent agronomique local."),
        }
