import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { LangueService } from '../../../core/services/langue.service';
import { LangueSelectorComponent } from '../../../shared/langue-selector/langue-selector.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LangueSelectorComponent],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  telephone = '';
  chargement = false;
  succes = '';
  erreur = '';

  constructor(
    private http: HttpClient,
    private router: Router,
    public langueService: LangueService
  ) {}

  onSubmit(): void {
    if (!this.telephone.trim()) return;
    this.chargement = true;
    this.erreur = '';
    this.succes = '';

    this.http.post(`${environment.apiUrl}/auth/forgot-password`, { telephone: this.telephone })
      .subscribe({
        next: () => {
          this.succes = 'Mot de passe temporaire envoyé par SMS !';
          this.chargement = false;
          setTimeout(() => this.router.navigate(['/login']), 2500);
        },
        error: (err) => {
          this.chargement = false;
          if (err.status === 404) {
            this.erreur = 'Aucun compte associé à ce numéro.';
          } else {
            this.erreur = 'Erreur serveur. Réessayez plus tard.';
          }
        }
      });
  }
}
