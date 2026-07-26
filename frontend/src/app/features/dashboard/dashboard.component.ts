import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DiagnosticService, Diagnostic } from '../../core/services/diagnostic.service';
import { ConseilService, ReponseConseil } from '../../core/services/conseil.service';
import { LangueService } from '../../core/services/langue.service';
import { LangueSelectorComponent } from '../../shared/langue-selector/langue-selector.component';
import { PaymentService } from '../../core/services/payment.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LangueSelectorComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  historique: Diagnostic[] = [];
  chargementHistorique = true;

  question = '';
  reponseConseil: ReponseConseil | null = null;
  chargementConseil = false;

  chargementAbonnement = false;
  erreurAbonnement: string | null = null;

  private readonly userId = 1;

  constructor(
    private diagnosticService: DiagnosticService,
    private conseilService: ConseilService,
    private paymentService: PaymentService,
    public langueService: LangueService
  ) {}

  ngOnInit(): void {
    this.diagnosticService.historique(this.userId).subscribe({
      next: (data) => {
        this.historique = data;
        this.chargementHistorique = false;
      },
      error: () => (this.chargementHistorique = false)
    });
  }

  poserQuestion(): void {
    if (!this.question.trim()) return;
    this.chargementConseil = true;
    this.conseilService.demander(this.question).subscribe({
      next: (res) => {
        this.reponseConseil = res;
        this.chargementConseil = false;
      },
      error: () => (this.chargementConseil = false)
    });
  }

  souscrireAbonnement(plan: string): void {
    this.chargementAbonnement = true;
    this.erreurAbonnement = null;

    this.paymentService.abonner(this.userId.toString(), plan).subscribe({
      next: (res) => {
        // Redirige l'utilisateur vers la page de paiement PayDunya
        window.location.href = res.url;
      },
      error: () => {
        this.chargementAbonnement = false;
        this.erreurAbonnement = 'Le service de paiement est momentanément indisponible. Réessayez plus tard.';
      }
    });
  }
}
