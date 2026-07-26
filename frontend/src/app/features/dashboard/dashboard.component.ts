import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DiagnosticService, Diagnostic } from '../../core/services/diagnostic.service';
import { ConseilService, ReponseConseil } from '../../core/services/conseil.service';
import { LangueService } from '../../core/services/langue.service';
import { LangueSelectorComponent } from '../../shared/langue-selector/langue-selector.component';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';

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

  nomUtilisateur = '';
  prenomUtilisateur = '';
  initialesUtilisateur = '';

  meteo = {
    temperature: 28,
    condition: 'Ensoleillé',
    humiditeSol: 45
  };

  private readonly userId = 1;

  constructor(
    private diagnosticService: DiagnosticService,
    private conseilService: ConseilService,
    private paymentService: PaymentService,
    private authService: AuthService,
    public langueService: LangueService
  ) {}

  ngOnInit(): void {
    this.chargerProfilUtilisateur();

    this.diagnosticService.historique(this.userId).subscribe({
      next: (data) => {
        this.historique = data;
        this.chargementHistorique = false;
      },
      error: () => (this.chargementHistorique = false)
    });
  }

  private chargerProfilUtilisateur(): void {
    const nomComplet = this.authService.getNomComplet();

    if (nomComplet) {
      this.nomUtilisateur = nomComplet;
      this.prenomUtilisateur = nomComplet.split(' ')[0];
      this.initialesUtilisateur = nomComplet
        .split(' ')
        .filter(mot => mot.length > 0)
        .slice(0, 2)
        .map(mot => mot[0].toUpperCase())
        .join('');
    } else {
      this.nomUtilisateur = 'Utilisateur';
      this.prenomUtilisateur = '';
      this.initialesUtilisateur = '?';
    }
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
        window.location.href = res.url;
      },
      error: () => {
        this.chargementAbonnement = false;
        this.erreurAbonnement = 'Le service de paiement est momentanément indisponible. Réessayez plus tard.';
      }
    });
  }
}
