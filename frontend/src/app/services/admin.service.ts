import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly baseUrl = 'http://localhost:8080/api';
  private readonly requestsUrl = `${this.baseUrl}/requests`;
  private readonly usersUrl = `${this.baseUrl}/users`;

  constructor(private http: HttpClient) {}

  getPendingRequests(): Observable<any[]> {
    return this.http.get<any[]>(`${this.requestsUrl}/pending`);
  }

  approveRequest(id: number, roleName: string): Observable<any> {
    return this.http.post(`${this.requestsUrl}/${id}/approve?roleName=${roleName}`, {});
  }

  rejectRequest(id: number, reason: string): Observable<any> {
    return this.http.post(`${this.requestsUrl}/${id}/reject?reason=${encodeURIComponent(reason)}`, {});
  }

  getApprovedUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.usersUrl}`);
  }

  updateUserRole(userId: number, roleName: string): Observable<any> {
    return this.http.put(`${this.usersUrl}/${userId}/role`, { roleName });
  }
}
