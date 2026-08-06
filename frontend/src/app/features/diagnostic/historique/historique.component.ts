import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DiagnosticService, Diagnostic } from '../../../core/services/diagnostic.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './historique.component.html',
  styleUrl: './historique.component.css'
})
export class HistoriqueComponent implements OnInit {
  diagnostics: Diagnostic[] = [];
  chargement = true;
  erreur = '';
  diagnosticSelectionne: Diagnostic | null = null;

  constructor(
    private diagnosticService: DiagnosticService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (!userId) { this.chargement = false; return; }

    this.diagnosticService.historique(userId).subscribe({
      next: (data) => {
        this.diagnostics = data;
        this.chargement = false;
      },
      error: () => {
        this.erreur = 'Impossible de charger l\'historique.';
        this.chargement = false;
      }
    });
  }

  ouvrirDetail(d: Diagnostic): void {
    this.diagnosticSelectionne = d;
  }

  fermerDetail(): void {
    this.diagnosticSelectionne = null;
  }

  getBadgeClass(confiance: number): string {
    if (confiance >= 0.8) return 'badge-vert';
    if (confiance >= 0.5) return 'badge-orange';
    return 'badge-rouge';
  }
}
