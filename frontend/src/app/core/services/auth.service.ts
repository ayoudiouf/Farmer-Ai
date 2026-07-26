import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuthResponse {
  token: string;
  telephone: string;
  nomComplet: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly tokenKey = 'farmerai_token';
  private readonly nomKey = 'farmerai_nom';
  private readonly telKey = 'farmerai_tel';

  constructor(private http: HttpClient) {}

  login(telephone: string, motDePasse: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, { telephone, motDePasse })
      .pipe(tap((res) => this.storeSession(res)));
  }

  register(payload: {
    telephone: string;
    nomComplet: string;
    motDePasse: string;
    region?: string;
    langue?: string;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, payload)
      .pipe(tap((res) => this.storeSession(res)));
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.nomKey);
    localStorage.removeItem(this.telKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getNomComplet(): string {
    return localStorage.getItem(this.nomKey) || '';
  }

  getTelephone(): string {
    return localStorage.getItem(this.telKey) || '';
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private storeSession(res: AuthResponse): void {
    localStorage.setItem(this.tokenKey, res.token);
    localStorage.setItem(this.nomKey, res.nomComplet);
    localStorage.setItem(this.telKey, res.telephone);
  }
}
