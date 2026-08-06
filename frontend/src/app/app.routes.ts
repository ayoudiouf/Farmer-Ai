import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'diagnostic',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/diagnostic/diagnostic.component').then(m => m.DiagnosticComponent)
  },
  {
    path: 'profil',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profil/profil.component').then(m => m.ProfilComponent)
  },
  {
    path: 'historique',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/diagnostic/historique/historique.component').then(m => m.HistoriqueComponent)
  },
  //{
   // path: 'parametres',
   // canActivate: [authGuard],
    //loadComponent: () =>
    //  import('./features/parametres/parametres.component').then(m => m.ParametresComponent)
  //},
  {
  path: 'parametres',
  redirectTo: 'profil',
  pathMatch: 'full'
},
  {
    path: 'a-propos',
    loadComponent: () =>
      import('./features/a-propos/a-propos.component').then(m => m.AProposComponent)
  },
  {
    path: 'conditions',
    loadComponent: () =>
      import('./features/conditions/conditions.component').then(m => m.ConditionsComponent)
  },

  { path: '**', component: LoginComponent }
];
