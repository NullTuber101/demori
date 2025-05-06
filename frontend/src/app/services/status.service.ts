import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Status {
  id: number;
  statusName: string;
  percentage: number;
}

@Injectable({
  providedIn: 'root',
})
export class StatusService {
  private apiUrl = 'http://localhost:8080/api/statuses';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  // Public access
  getStatuses(): Observable<Status[]> {
    return this.http.get<Status[]>(this.apiUrl);
  }

  // Authenticated only
  addStatus(status: Status): Observable<Status> {
    return this.http.post<Status>(this.apiUrl, status, {
      headers: this.getHeaders()
    });
  }

  updateStatus(status: Status): Observable<Status> {
    return this.http.put<Status>(`${this.apiUrl}/${status.id}`, status, {
      headers: this.getHeaders()
    });
  }

  deleteStatus(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }
}
