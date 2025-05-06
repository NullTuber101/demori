import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service'; 

export interface Area {
  id: number;
  name: string;
  leadName: string;
  leadEmail: string;
}

@Injectable({
  providedIn: 'root',
})
export class AreaService {
  private apiUrl = 'http://localhost:8080/api/areas';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  getAreas(): Observable<Area[]> {
    return this.http.get<Area[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  addArea(area: Area): Observable<Area> {
    return this.http.post<Area>(this.apiUrl, area, { headers: this.getHeaders() });
  }

  updateArea(area: Area): Observable<Area> {
    return this.http.put<Area>(`${this.apiUrl}/${area.id}`, area, { headers: this.getHeaders() });
  }

  deleteArea(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}
