import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <router-outlet></router-outlet>
    <footer class="app-footer">
      <p>&copy; {{ currentYear }} FarmerAI — Créé par Ayou Service Tech. Tous droits réservés.</p>
    </footer>
  `,
  styles: [`
    .app-footer {
      text-align: center;
      padding: 1.5rem 1rem;
      font-size: 0.85rem;
      color: #6b7280;
      border-top: 1px solid #e5e7eb;
      margin-top: 2rem;
    }
  `]
})
export class AppComponent {
  currentYear = new Date().getFullYear();
}
