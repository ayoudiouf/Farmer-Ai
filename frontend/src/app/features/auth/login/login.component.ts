import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LangueService } from '../../../core/services/langue.service';
import { LangueSelectorComponent } from '../../../shared/langue-selector/langue-selector.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LangueSelectorComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  telephone = '';
  motDePasse = '';
  erreur = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    public langueService: LangueService
  ) {}

  onSubmit(): void {
    this.erreur = '';
    this.authService.login(this.telephone, this.motDePasse).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => (this.erreur = this.langueService.t('erreur_login'))
    });
  }
}
