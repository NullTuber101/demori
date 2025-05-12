import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  redirectToLogin(redirectToLogin: any) {
      throw new Error('Method not implemented.');
  }
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  login(credentials: { brid: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  signup(request: {
    name: string;
    brid: string;
    email: string;
    password: string;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}/requests/signup`, request);
  }

  // Save token to local storage
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Get token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Decode token and extract role
  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload?.role || null;
    } catch {
      return null;
    }
  }

  // Get BRID (username)
  getBrid(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload?.sub || null;
    } catch {
      return null;
    }
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Logout user
  logout(): void {
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
