import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LienPaiement {
  url: string;
  tokenFacture: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly apiUrl = `${environment.apiUrl}/paiements`;

  constructor(private http: HttpClient) {}

  abonner(userId: string, plan: string): Observable<LienPaiement> {
    return this.http.post<LienPaiement>(`${this.apiUrl}/abonner`, { userId, plan });
  }
}
