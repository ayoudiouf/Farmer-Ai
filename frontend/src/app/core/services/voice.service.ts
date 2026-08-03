import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReponseAppel {
  entries: { phoneNumber: string; status: string; sessionId: string }[];
  errorMessage: string;
}

@Injectable({ providedIn: 'root' })
export class VoiceService {
  private apiUrl = '/api/voix/appeler';

  constructor(private http: HttpClient) {}

  appeler(numero: string): Observable<ReponseAppel> {
    return this.http.post<ReponseAppel>(this.apiUrl, { numero });
  }
}
