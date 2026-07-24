"""
Répartit le dataset brut collecté en dossiers train/ et val/
(80/20 par défaut), prêts pour l'entraînement.

Usage :
    python scripts/split_dataset.py --input dataset_brut --output dataset_split
"""
import argparse
import os
import random
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from app.model.labels import CLASSES

IMAGE_EXTENSIONS = (".jpg", ".jpeg", ".png")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", default="dataset_brut")
    parser.add_argument("--output", default="dataset_split")
    parser.add_argument("--ratio-val", type=float, default=0.2)
    parser.add_argument("--seed", type=int, default=42)
    args = parser.parse_args()

    random.seed(args.seed)

    for classe in CLASSES:
        dossier_source = os.path.join(args.input, classe)
        if not os.path.isdir(dossier_source):
            continue

        fichiers = [f for f in os.listdir(dossier_source) if f.lower().endswith(IMAGE_EXTENSIONS)]
        random.shuffle(fichiers)

        nb_val = max(1, int(len(fichiers) * args.ratio_val)) if fichiers else 0
        fichiers_val = fichiers[:nb_val]
        fichiers_train = fichiers[nb_val:]

        for sous_ensemble, liste in [("train", fichiers_train), ("val", fichiers_val)]:
            dossier_cible = os.path.join(args.output, sous_ensemble, classe)
            os.makedirs(dossier_cible, exist_ok=True)
            for f in liste:
                shutil.copy2(os.path.join(dossier_source, f), os.path.join(dossier_cible, f))

        print(f"{classe:<35} train={len(fichiers_train):<6} val={len(fichiers_val)}")

    print(f"\nDataset réparti dans '{args.output}/train' et '{args.output}/val'.")
    print("Lance ensuite : python -m app.train --data-dir dataset_split/train")


if __name__ == "__main__":
    main()
