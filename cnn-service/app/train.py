"""
Entraînement du modèle CNN de détection de maladies.

Usage :
    python -m app.train --data-dir chemin/vers/dataset --epochs 15

Le dataset doit être organisé ainsi (format standard PlantVillage) :
    dataset/
      sain/
        img1.jpg ...
      mil_mildiou/
        img1.jpg ...
      arachide_cercosporiose/
        img1.jpg ...
      ... (voir app/model/labels.py pour la liste complète des classes)

À la fin de l'entraînement, le fichier app/model/checkpoint.pt est généré :
le service /predict le charge automatiquement au prochain démarrage.
"""
import argparse

import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from torchvision import datasets, transforms

from app.model.architecture import PlantDiseaseCNN
from app.model.labels import CLASSES

IMAGE_SIZE = 128


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--data-dir", required=True, help="Dossier du dataset (1 sous-dossier par classe)")
    parser.add_argument("--epochs", type=int, default=15)
    parser.add_argument("--batch-size", type=int, default=32)
    parser.add_argument("--lr", type=float, default=1e-3)
    parser.add_argument("--output", default="app/model/checkpoint.pt")
    args = parser.parse_args()

    transform = transforms.Compose([
        transforms.Resize((IMAGE_SIZE, IMAGE_SIZE)),
        transforms.RandomHorizontalFlip(),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
    ])

    dataset = datasets.ImageFolder(args.data_dir, transform=transform)

    # Vérifie que les dossiers du dataset correspondent aux classes attendues
    missing = set(CLASSES) - set(dataset.classes)
    if missing:
        print(f"Attention : classes absentes du dataset : {missing}")

    loader = DataLoader(dataset, batch_size=args.batch_size, shuffle=True)

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model = PlantDiseaseCNN(num_classes=len(CLASSES)).to(device)
    optimizer = torch.optim.Adam(model.parameters(), lr=args.lr)
    criterion = nn.CrossEntropyLoss()

    model.train()
    for epoch in range(args.epochs):
        total_loss, correct, total = 0.0, 0, 0
        for images, labels in loader:
            images, labels = images.to(device), labels.to(device)

            optimizer.zero_grad()
            outputs = model(images)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()

            total_loss += loss.item()
            correct += (outputs.argmax(1) == labels).sum().item()
            total += labels.size(0)

        print(f"Epoch {epoch+1}/{args.epochs} - perte: {total_loss/len(loader):.4f} - précision: {correct/total:.2%}")

    torch.save(model.state_dict(), args.output)
    print(f"Modèle sauvegardé : {args.output}")


if __name__ == "__main__":
    main()
