import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ProjectService } from '../services/project.service';
import { AreaService } from '../services/area.service';
import { StatusService } from '../services/status.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AlertComponent } from '../alert/alert.component';
import { Project } from '../models/project.model';

@Component({
  selector: 'app-project-edit',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './project-edit.component.html',
  styleUrls: ['./project-edit.component.css']
})
export class ProjectEditComponent implements OnInit {
  projectForm!: FormGroup;
  areaList: any[] = [];
  statusList: any[] = [];
  projectId: string = '';

  isLoggedIn: boolean = false;
  userRole: string = 'VIEWER';

  private projectService = inject(ProjectService);
  private areaService = inject(AreaService);
  private statusService = inject(StatusService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.projectId = this.activatedRoute.snapshot.paramMap.get('id')!;

    this.projectForm = this.fb.group({
      areaId: ['', Validators.required],
      projectName: ['', Validators.required],
      description: [''],
      developer: ['', Validators.required],
      jira: [''],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      statusId: ['', Validators.required]
    });

    // ✅ Decode user info from token
    const token = localStorage.getItem('token');
    this.isLoggedIn = !!token;

    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userRole = payload.role || 'VIEWER';
      } catch {
        this.userRole = 'VIEWER';
      }
    }

    this.loadAreaList();
    this.loadStatusList();
    this.loadProjectDetails();
  }

  loadAreaList(): void {
    this.areaService.getAreas().subscribe({
      next: (data) => (this.areaList = data),
      error: () => {
        this.openAlert('error', 'Load Error', 'Failed to load areas.');
      }
    });
  }

  loadStatusList(): void {
    this.statusService.getStatuses().subscribe({
      next: (data) => (this.statusList = data),
      error: () => {
        this.openAlert('error', 'Load Error', 'Failed to load statuses.');
      }
    });
  }

  loadProjectDetails(): void {
    const projectIdAsNumber = +this.projectId;
    this.projectService.getProjectById(projectIdAsNumber).subscribe({
      next: (project: Project) => {
        this.projectForm.patchValue({
          areaId: project.area?.id,
          projectName: project.projectName,
          description: project.description,
          developer: project.developer,
          jira: project.jira,
          startDate: new Date(project.startDate),
          endDate: new Date(project.endDate),
          statusId: project.status?.id
        });
      },
      error: () => {
        this.openAlert('error', 'Load Error', 'Failed to load project details.');
      }
    });
  }

  openAlert(type: 'success' | 'info' | 'error' | 'warning', title: string, message: string) {
    if (type === 'error') {
      this.dialog.open(AlertComponent, {
        data: { title, message }
      });
    } else {
      this.snackBar.open(message, 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'bottom',
        panelClass: ['custom-snackbar']
      });
    }
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const formValue = this.projectForm.value;
      const updatedProject = {
        projectName: formValue.projectName,
        description: formValue.description,
        developer: formValue.developer,
        jira: formValue.jira,
        startDate: formValue.startDate,
        endDate: formValue.endDate,
        area: { id: formValue.areaId },
        status: { id: formValue.statusId }
      };

      const projectIdAsNumber = +this.projectId;

      this.projectService.editProject(projectIdAsNumber, updatedProject).subscribe({
        next: () => {
          this.openAlert('success', 'Success', 'Project updated successfully!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          const message = err.error.message?.[0]?.split(":")[1]?.trim() || 'Update failed.';
          this.openAlert('error', 'Save Error', message);
        }
      });
    } else {
      this.openAlert('error', 'Validation Error', 'Please fill all required fields correctly.');
    }
  }
}
