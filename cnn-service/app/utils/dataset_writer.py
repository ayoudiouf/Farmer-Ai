import os
import uuid


def enregistrer_photo_dataset(image_bytes: bytes, classe: str, agent_id: str, base_dir: str = "dataset_brut") -> str:
    dossier = os.path.join(base_dir, classe)
    os.makedirs(dossier, exist_ok=True)

    nom_fichier = f"{agent_id}_{uuid.uuid4().hex[:8]}.jpg"
    chemin = os.path.join(dossier, nom_fichier)

    with open(chemin, "wb") as f:
        f.write(image_bytes)

    return chemin
