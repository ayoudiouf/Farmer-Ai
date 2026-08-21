import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LangueService } from '../../core/services/langue.service';
import { LangueSelectorComponent } from '../../shared/langue-selector/langue-selector.component';

@Component({
  selector: 'app-accueil',
  standalone: true,
  imports: [CommonModule, RouterLink, LangueSelectorComponent],
  templateUrl: './accueil.component.html',
  styleUrl: './accueil.component.css'
})
export class AccueilComponent {
  menuOuvert = false;
  menuConnexionOuvert = false;

  constructor(public langueService: LangueService) {}

  basculerMenu(event: Event): void {
    event.stopPropagation();
    this.menuOuvert = !this.menuOuvert;
    this.menuConnexionOuvert = false;
  }

  basculerMenuConnexion(event: Event): void {
    event.stopPropagation();
    this.menuConnexionOuvert = !this.menuConnexionOuvert;
    this.menuOuvert = false;
  }

  @HostListener('document:click')
  fermerMenus(): void {
    this.menuOuvert = false;
    this.menuConnexionOuvert = false;
  }
}
