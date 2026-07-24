"""
Endpoint de contribution au dataset - utilisé par les agents terrain pendant
le pilote (3 coopératives) pour envoyer des photos déjà identifiées par un
agronome, directement rangées dans la bonne classe. Ce n'est PAS le même
flux que /predict (qui sert les agriculteurs en production).
"""
from fastapi import APIRouter, File, Form, HTTPException, UploadFile

from app.model.labels import CLASSES
from app.utils.dataset_writer import enregistrer_photo_dataset

router = APIRouter(prefix="/dataset", tags=["dataset"])


@router.get("/classes")
def lister_classes():
    """Liste des classes disponibles, pour peupler un menu déroulant côté app agent."""
    return {"classes": CLASSES}


@router.post("/contribuer")
async def contribuer(
    file: UploadFile = File(...),
    classe: str = Form(...),
    agent_id: str = Form(default="inconnu"),
):
    if classe not in CLASSES:
        raise HTTPException(
            status_code=400,
            detail=f"Classe inconnue '{classe}'. Classes valides : {CLASSES}",
        )
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Le fichier doit être une image.")

    image_bytes = await file.read()
    chemin = enregistrer_photo_dataset(image_bytes, classe, agent_id)

    return {"statut": "enregistré", "chemin": chemin, "classe": classe}
