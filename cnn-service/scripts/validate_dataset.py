"""
Vérifie l'état du dataset avant de lancer un entraînement :
- Combien de photos par classe ?
- Y a-t-il des classes vides ou trop peu fournies ?
- Y a-t-il des fichiers corrompus ?

Usage :
    python scripts/validate_dataset.py --data-dir dataset_brut
"""
import argparse
import os
import sys

from PIL import Image, UnidentifiedImageError

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from app.model.labels import CLASSES

MIN_RECOMMANDE = 50  # seuil minimum réaliste par classe pour un premier entraînement correct
IMAGE_EXTENSIONS = (".jpg", ".jpeg", ".png")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data-dir", default="dataset_brut")
    args = parser.parse_args()

    print(f"Analyse du dataset : {args.data_dir}\n")
    print(f"{'Classe':<35}{'Photos':<10}{'Corrompues':<12}{'Statut'}")
    print("-" * 75)

    total = 0
    classes_insuffisantes = []
    classes_vides = []

    for classe in CLASSES:
        dossier = os.path.join(args.data_dir, classe)
        if not os.path.isdir(dossier):
            print(f"{classe:<35}{'0':<10}{'-':<12}MANQUANT (dossier absent)")
            classes_vides.append(classe)
            continue

        fichiers = [f for f in os.listdir(dossier) if f.lower().endswith(IMAGE_EXTENSIONS)]
        corrompus = 0
        for f in fichiers:
            try:
                with Image.open(os.path.join(dossier, f)) as img:
                    img.verify()
            except (UnidentifiedImageError, OSError):
                corrompus += 1

        nb_valides = len(fichiers) - corrompus
        total += nb_valides

        if nb_valides == 0:
            statut = "VIDE"
            classes_vides.append(classe)
        elif nb_valides < MIN_RECOMMANDE:
            statut = f"INSUFFISANT (min {MIN_RECOMMANDE} recommandé)"
            classes_insuffisantes.append(classe)
        else:
            statut = "OK"

        print(f"{classe:<35}{nb_valides:<10}{corrompus:<12}{statut}")

    print("-" * 75)
    print(f"Total photos valides : {total}\n")

    if classes_vides:
        print(f"⚠ Classes vides (aucune photo) : {', '.join(classes_vides)}")
    if classes_insuffisantes:
        print(f"⚠ Classes sous le seuil recommandé : {', '.join(classes_insuffisantes)}")

    if not classes_vides and not classes_insuffisantes:
        print("✓ Dataset prêt pour un premier entraînement (python -m app.train --data-dir ...).")
    else:
        print("→ Continue la collecte avant de lancer l'entraînement pour de meilleurs résultats.")


if __name__ == "__main__":
    main()
