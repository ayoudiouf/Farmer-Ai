import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Diagnostic {
  id: number;
  cultureConcernee: string;
  maladieDetectee: string;
  indiceConfiance: number;
  recommandation: string;
  dateAnalyse: string;
}

@Injectable({ providedIn: 'root' })
export class DiagnosticService {
  private readonly apiUrl = `${environment.apiUrl}/diagnostics`;

  constructor(private http: HttpClient) {}

  analyserPhoto(photo: File, culture: string, userId: number): Observable<Diagnostic> {
    const formData = new FormData();
    formData.append('photo', photo);
    formData.append('culture', culture);
    formData.append('userId', userId.toString());
    return this.http.post<Diagnostic>(`${this.apiUrl}/analyser`, formData);
  }

  historique(userId: number): Observable<Diagnostic[]> {
    return this.http.get<Diagnostic[]>(`${this.apiUrl}/utilisateur/${userId}`);
  }
}
