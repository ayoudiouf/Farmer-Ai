import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Profil {
  telephone: string;
  nomComplet: string;
  region: string;
  langue: string;
  photoUrl: string;
}

@Injectable({ providedIn: 'root' })
export class ProfilService {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  monProfil(): Observable<Profil> {
    return this.http.get<Profil>(`${this.apiUrl}/me`);
  }

  mettreAJour(payload: { nomComplet?: string; region?: string; langue?: string }): Observable<Profil> {
    return this.http.put<Profil>(`${this.apiUrl}/me`, payload);
  }

  changerMotDePasse(ancienMotDePasse: string, nouveauMotDePasse: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/me/mot-de-passe`, { ancienMotDePasse, nouveauMotDePasse });
  }

  uploaderPhoto(fichier: File): Observable<Profil> {
    const formData = new FormData();
    formData.append('photo', fichier);
    return this.http.post<Profil>(`${this.apiUrl}/me/photo`, formData);
  }
}
