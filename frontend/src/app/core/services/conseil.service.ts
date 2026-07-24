import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReponseConseil {
  reponse: string;
  sourcesUtilisees: string[];
}

@Injectable({ providedIn: 'root' })
export class ConseilService {
  private readonly apiUrl = `${environment.apiUrl}/conseils`;

  constructor(private http: HttpClient) {}

  demander(question: string, langue = 'français'): Observable<ReponseConseil> {
    return this.http.post<ReponseConseil>(`${this.apiUrl}/demander`, { question, langue });
  }
}
