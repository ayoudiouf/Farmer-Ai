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
import { VoiceService } from '../../core/services/voice.service';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, LangueSelectorComponent, SidebarComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  historique: Diagnostic[] = [];
  chargementHistorique = true;
  sidebarOuverte = false;

  question = '';
  reponseConseil: ReponseConseil | null = null;
  chargementConseil = false;

  chargementAbonnement = false;
  erreurAbonnement: string | null = null;

  chargementAppel = false;
  erreurAppel: string | null = null;
  appelConfirme = false;

  nomUtilisateur = '';
  prenomUtilisateur = '';
  initialesUtilisateur = '';

  meteo = {
    temperature: 28,
    condition: 'Ensoleillé',
    humiditeSol: 45
  };

private userId!: number;

constructor(
  private diagnosticService: DiagnosticService,
  private conseilService: ConseilService,
  private paymentService: PaymentService,
  private authService: AuthService,
  private voiceService: VoiceService,
  public langueService: LangueService
) {}

ngOnInit(): void {
  this.chargerProfilUtilisateur();

  const id = this.authService.getUserId();
  if (!id) {
    this.chargementHistorique = false;
    return;
  }
  this.userId = id;

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

  appelerConseiller(): void {
    this.erreurAppel = null;
    this.appelConfirme = false;

    const numero = this.authService.getTelephone();
    if (!numero) {
      this.erreurAppel = "Aucun numéro de téléphone associé à votre profil.";
      return;
    }

    this.chargementAppel = true;
    this.voiceService.appeler(numero).subscribe({
      next: () => {
        this.chargementAppel = false;
        this.appelConfirme = true;
      },
      error: () => {
        this.chargementAppel = false;
        this.erreurAppel = "Impossible de lancer l'appel pour le moment.";
      }
    });
  }

  paiementActif = false;
  // ⚠️ passer à true une fois Stripe / PayDunya / etc. réellement intégré

  souscrireAbonnement(plan: string): void {
    this.erreurAbonnement = null;

    if (!this.paiementActif) {
      this.erreurAbonnement = "🚧 Le paiement en ligne est en cours d'intégration. Cette fonctionnalité sera bientôt disponible. Merci de votre patience !";
      return;
    }

    this.chargementAbonnement = true;
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
