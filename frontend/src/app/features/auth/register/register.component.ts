import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LangueService, Langue } from '../../../core/services/langue.service';
import { LangueSelectorComponent } from '../../../shared/langue-selector/langue-selector.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LangueSelectorComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  telephone = '';
  nomComplet = '';
  motDePasse = '';
  region = '';
  langueChoisie: Langue = 'FRANCAIS';
  erreur = '';
  chargement = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    public langueService: LangueService
  ) {}

  onSubmit(): void {
    this.erreur = '';
    this.chargement = true;
    this.authService
      .register({
        telephone: this.telephone,
        nomComplet: this.nomComplet,
        motDePasse: this.motDePasse,
        region: this.region,
        langue: this.langueChoisie
      })
      .subscribe({
        next: () => {
          this.langueService.changerLangue(this.langueChoisie);
          this.router.navigate(['/dashboard']);
        },
        error: () => {
          this.erreur = this.langueService.t('erreur_inscription');
          this.chargement = false;
        }
      });
  }
}
