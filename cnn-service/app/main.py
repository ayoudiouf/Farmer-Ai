"""
FarmerAI - Microservice de détection de maladies (CNN)
Service indépendant appelé par le backend Spring Boot via HTTP.
"""
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from app.model.predictor import DiseasePredictor
from app.dataset_routes import router as dataset_router

app = FastAPI(title="FarmerAI CNN Service", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],  # backend Spring Boot
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(dataset_router)

predictor = DiseasePredictor()


class PredictionResponse(BaseModel):
    maladie_detectee: str
    indice_confiance: float
    recommandation: str


@app.get("/health")
def health():
    return {"status": "ok", "modele_charge": predictor.is_ready()}


@app.post("/predict", response_model=PredictionResponse)
async def predict(file: UploadFile = File(...), culture: str = "inconnue"):
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Le fichier doit être une image.")

    image_bytes = await file.read()
    result = predictor.predict(image_bytes, culture=culture)

    return PredictionResponse(
        maladie_detectee=result["maladie"],
        indice_confiance=result["confiance"],
        recommandation=result["recommandation"],
    )
