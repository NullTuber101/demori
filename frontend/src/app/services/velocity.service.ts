import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service'; // <-- Make sure this provides getToken()

@Injectable({
  providedIn: 'root'
})
export class VelocityService {
  private baseUrl = 'http://localhost:8080/api/velocities';
  private areaUrl = 'http://localhost:8080/api/scrum-areas';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  // ──────── SCRUM AREA ────────

  getScrumAreas(): Observable<any[]> {
    return this.http.get<any[]>(this.areaUrl); // Public
  }

  addScrumArea(scrumArea: any): Observable<any> {
    return this.http.post(this.areaUrl, scrumArea, { headers: this.getHeaders() });
  }

  updateScrumArea(id: number, scrumArea: any): Observable<any> {
    return this.http.put(`${this.areaUrl}/${id}`, scrumArea, { headers: this.getHeaders() });
  }

  deleteScrumArea(id: number): Observable<void> {
    return this.http.delete<void>(`${this.areaUrl}/${id}`, { headers: this.getHeaders() });
  }

  // ──────── VELOCITY ────────

  getVelocities(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl); // Public
  }

  getVelocitiesByScrumArea(areaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/scrum-area/${areaId}`); // Public
  }

  addVelocity(areaId: number, velocity: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/${areaId}`, velocity, { headers: this.getHeaders() });
  }

  updateVelocity(id: number, velocity: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, velocity, { headers: this.getHeaders() });
  }

  deleteVelocity(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  // ──────── VELOCITY CHART ────────

  getVelocityChartData(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/chart-data`); // Public
  }
}
