import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {
  @Input() isOpen = false;
  @Output() fermer = new EventEmitter<void>();

  nomUtilisateur = '';
  initialesUtilisateur = '';

  menuItems = [
    { label: 'Dashboard',                 icon: '🏠', route: '/dashboard' },
    { label: 'Historique des diagnostics', icon: '📋', route: '/historique' },
    { label: 'Paramètres', icon: '⚙️', route: '/profil' },
    { label: 'À propos de nous',          icon: 'ℹ️', route: '/a-propos' },
    { label: "Conditions d'utilisation",  icon: '📄', route: '/conditions' },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const nomComplet = this.authService.getNomComplet() || 'Utilisateur';
    this.nomUtilisateur = nomComplet;
    this.initialesUtilisateur = nomComplet
      .split(' ')
      .filter(m => m.length > 0)
      .slice(0, 2)
      .map(m => m[0].toUpperCase())
      .join('');
  }

  close(): void {
    this.fermer.emit();
  }

  logout(): void {
    this.authService.logout(); // ou localStorage.clear() si pas de méthode logout
    this.router.navigate(['/login']);
    this.close();
  }
}
