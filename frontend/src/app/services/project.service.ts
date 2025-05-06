import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Area } from '../models/area.model';
import { Status } from '../models/status.model';
import { AuthService } from './auth.service';

export interface Project {
  id: number;
  projectName: string;
  description: string;
  developer: string;
  jira: string;
  startDate: string;
  endDate: string;
  area: Area;
  status: Status;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken(); // implement getToken() in AuthService
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  // Get all areas (can be public)
  getAreas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/areas`);
  }

  // Get all statuses (can be public)
  getStatuses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/statuses`);
  }

  // Add a new project (requires auth)
  addProject(projectData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/projects`, projectData, { headers: this.getHeaders() });
  }

  // Get all projects (can be public or protected)
  getAllProjects(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/projects`);
  }

  // Edit a project (requires auth)
  editProject(projectId: number, projectData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/projects/${projectId}`, projectData, { headers: this.getHeaders() });
  }

  // Delete a project (requires auth)
  deleteProject(projectId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/projects/${projectId}`, { headers: this.getHeaders() });
  }

  // Get a project by ID (can be public)
  getProjectById(projectId: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/projects/${projectId}`);
  }
}
