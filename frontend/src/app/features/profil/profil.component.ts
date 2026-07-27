import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfilService, Profil } from '../../core/services/profil.service';
import { AuthService } from '../../core/services/auth.service';
import { LangueService } from '../../core/services/langue.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit {
  profil: Profil | null = null;
  chargement = true;

  nomComplet = '';
  region = '';
  langue = '';

  ancienMotDePasse = '';
  nouveauMotDePasse = '';
  confirmationMotDePasse = '';

  photoSelectionnee: File | null = null;
  photoApercu: string | null = null;

  messageSucces = '';
  messageErreur = '';
  messageErreurMotDePasse = '';
  chargementEnregistrement = false;
  chargementMotDePasse = false;
  chargementPhoto = false;

  constructor(
    private profilService: ProfilService,
    private authService: AuthService,
    public langueService: LangueService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.profilService.monProfil().subscribe({
      next: (data) => {
        this.profil = data;
        this.nomComplet = data.nomComplet;
        this.region = data.region || '';
        this.langue = data.langue || 'FRANCAIS';
        this.chargement = false;
      },
      error: () => {
        this.chargement = false;
        this.messageErreur = 'Impossible de charger votre profil.';
      }
    });
  }

  get initiales(): string {
    if (!this.nomComplet) return '?';
    return this.nomComplet
      .split(' ')
      .filter(m => m.length > 0)
      .slice(0, 2)
      .map(m => m[0].toUpperCase())
      .join('');
  }

  get urlPhotoComplete(): string | null {
    if (this.photoApercu) return this.photoApercu;
    if (this.profil?.photoUrl) return `${'/photos/'}${this.profil.photoUrl}`;
    return null;
  }

  enregistrerProfil(): void {
    this.messageSucces = '';
    this.messageErreur = '';
    this.chargementEnregistrement = true;

    this.profilService.mettreAJour({
      nomComplet: this.nomComplet,
      region: this.region,
      langue: this.langue
    }).subscribe({
      next: (data) => {
        this.profil = data;
        this.chargementEnregistrement = false;
        this.messageSucces = 'Profil mis à jour avec succès.';
      },
      error: () => {
        this.chargementEnregistrement = false;
        this.messageErreur = 'Impossible de mettre à jour le profil.';
      }
    });
  }

  onPhotoSelectionnee(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.photoSelectionnee = input.files[0];
      const reader = new FileReader();
      reader.onload = () => (this.photoApercu = reader.result as string);
      reader.readAsDataURL(this.photoSelectionnee);
    }
  }

  uploaderPhoto(): void {
    if (!this.photoSelectionnee) return;
    this.chargementPhoto = true;

    this.profilService.uploaderPhoto(this.photoSelectionnee).subscribe({
      next: (data) => {
        this.profil = data;
        this.chargementPhoto = false;
        this.photoSelectionnee = null;
      },
      error: () => {
        this.chargementPhoto = false;
        this.messageErreur = "Impossible d'envoyer la photo.";
      }
    });
  }

  changerMotDePasse(): void {
    this.messageErreurMotDePasse = '';

    if (this.nouveauMotDePasse !== this.confirmationMotDePasse) {
      this.messageErreurMotDePasse = 'Les mots de passe ne correspondent pas.';
      return;
    }
    if (this.nouveauMotDePasse.length < 6) {
      this.messageErreurMotDePasse = 'Le mot de passe doit contenir au moins 6 caractères.';
      return;
    }

    this.chargementMotDePasse = true;
    this.profilService.changerMotDePasse(this.ancienMotDePasse, this.nouveauMotDePasse).subscribe({
      next: () => {
        this.chargementMotDePasse = false;
        this.ancienMotDePasse = '';
        this.nouveauMotDePasse = '';
        this.confirmationMotDePasse = '';
        this.messageSucces = 'Mot de passe changé avec succès.';
      },
      error: (err) => {
        this.chargementMotDePasse = false;
        this.messageErreurMotDePasse = err.error || 'Ancien mot de passe incorrect.';
      }
    });
  }

  retour(): void {
    this.router.navigate(['/dashboard']);
  }
}
