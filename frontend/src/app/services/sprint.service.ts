import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface Sprint {
  id?: number;
  sprintName: string;
  sprintStartDate: Date | string;
  sprintEndDate: Date | string;
  sprintJira: string;
  sprintDescription: string;
  assignedTo: string;
  sprintFor: {
    id: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class SprintService {
  private baseUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  // Create sprint (needs auth)
  createSprint(projectId: number, sprint: Sprint): Observable<Sprint> {
    return this.http.post<Sprint>(
      `${this.baseUrl}/${projectId}/sprints`,
      sprint,
      { headers: this.getHeaders() }
    );
  }

  // Get all sprints (can be public)
  getSprints(projectId: number): Observable<Sprint[]> {
    return this.http.get<Sprint[]>(`${this.baseUrl}/${projectId}/sprints`);
  }

  // Get single sprint (can be public)
  getSprintById(sprintId: number): Observable<Sprint> {
    return this.http.get<Sprint>(`${this.baseUrl}/sprints/${sprintId}`);
  }

  // Update sprint (needs auth)
  updateSprint(sprintId: number, sprint: Sprint): Observable<Sprint> {
    return this.http.put<Sprint>(
      `${this.baseUrl}/sprints/${sprintId}`,
      sprint,
      { headers: this.getHeaders() }
    );
  }

  // Delete sprint (needs auth)
  deleteSprint(sprintId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/sprints/${sprintId}`,
      { headers: this.getHeaders() }
    );
  }
}
