import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Langue, LangueService } from '../../core/services/langue.service';

@Component({
  selector: 'app-langue-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './langue-selector.component.html',
  styleUrl: './langue-selector.component.css'
})
export class LangueSelectorComponent {
  ouvert = false;

  constructor(public langueService: LangueService) {}

  basculer(event: Event): void {
    event.stopPropagation();
    this.ouvert = !this.ouvert;
  }

  choisir(code: Langue, event: Event): void {
    event.stopPropagation();
    this.langueService.changerLangue(code);
    this.ouvert = false;
  }

  @HostListener('document:click')
  fermer(): void {
    this.ouvert = false;
  }
}
