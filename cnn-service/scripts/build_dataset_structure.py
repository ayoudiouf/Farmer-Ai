"""
Crée automatiquement l'arborescence de dossiers attendue pour le dataset,
avec un sous-dossier par classe (voir app/model/labels.py).

Usage :
    python scripts/build_dataset_structure.py --output dataset_brut
"""
import argparse
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from app.model.labels import CLASSES


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", default="dataset_brut", help="Dossier racine du dataset")
    args = parser.parse_args()

    for classe in CLASSES:
        chemin = os.path.join(args.output, classe)
        os.makedirs(chemin, exist_ok=True)
        # Fichier .gitkeep pour que le dossier vide soit visible/trackable
        with open(os.path.join(chemin, ".gitkeep"), "w") as f:
            f.write("")

    print(f"Arborescence créée dans '{args.output}/' avec {len(CLASSES)} classes :")
    for classe in CLASSES:
        print(f"  - {classe}")
    print("\nDépose tes photos collectées sur le terrain dans le dossier correspondant à chaque classe.")


if __name__ == "__main__":
    main()
