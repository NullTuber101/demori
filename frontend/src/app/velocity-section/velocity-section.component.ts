import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { VelocityService } from '../services/velocity.service';
import { GenericDialogComponent } from '../generic-dialog/generic-dialog.component';
import { AlertComponent } from '../alert/alert.component';

@Component({
  selector: 'app-velocity-section',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    FormsModule
  ],
  templateUrl: './velocity-section.component.html',
  styleUrls: ['./velocity-section.component.css'],
  providers: [VelocityService]
})
export class VelocitySectionComponent implements OnInit {
  scrumAreaForm!: FormGroup;
  velocityForm!: FormGroup;
  scrumAreas: any[] = [];
  velocities: any[] = [];
  editingScrumAreaId: number | null = null;
  editingVelocityId: number | null = null;
  selectedScrumAreaId: number | null = null;
  isLoggedIn: boolean = false;
  userRole: string = 'VIEWER';

  constructor(
    private fb: FormBuilder,
    private velocityService: VelocityService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.scrumAreaForm = this.fb.group({
      areaName: ['', Validators.required],
      scrumMaster: ['', Validators.required],
      scrumTeam: ['', Validators.required],
      boardId: ['', Validators.required]
    });

    this.velocityForm = this.fb.group({
      sprintName: ['', Validators.required],
      velocity: [0, [Validators.required, Validators.min(1)]],
      sprintEndDate: ['', Validators.required],
      scrumAreaId: [null, Validators.required]
    });

    this.loadScrumAreas();
    this.loadVelocities();

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
  }

  openDialog(title: string, message: string) {
    this.dialog.open(AlertComponent, {
      data: { title, message }
    });
  }

  loadScrumAreas() {
    this.velocityService.getScrumAreas().subscribe({
      next: (data) => this.scrumAreas = data,
      error: () => this.openDialog('Error', 'Failed to load Scrum Areas.')
    });
  }

  loadVelocities() {
    this.velocityService.getVelocities().subscribe({
      next: (data) => this.velocities = data,
      error: () => this.openDialog('Error', 'Failed to load Velocities.')
    });
  }

  saveScrumArea() {
    if (this.scrumAreaForm.invalid || !this.isLoggedIn || this.userRole === 'VIEWER') {
      this.scrumAreaForm.markAllAsTouched();
      this.openDialog('Validation Error', 'Please fill all required Scrum Area fields.');
      return;
    }

    if (this.editingScrumAreaId) {
      this.velocityService.updateScrumArea(this.editingScrumAreaId, this.scrumAreaForm.value).subscribe({
        next: () => {
          this.snackBar.open('Scrum Area updated successfully!', 'Close', { duration: 3000 });
          this.scrumAreaForm.reset();
          this.editingScrumAreaId = null;
          this.loadScrumAreas();
        },
        error: (err) => {
          const err_msg=err.error.message[0].split(":")[1]?.trim() || 'Failed to add Scrum Area.'
          this.openDialog('Error', err_msg)
        }
      });
    } else {
      this.velocityService.addScrumArea(this.scrumAreaForm.value).subscribe({
        next: () => {
          this.snackBar.open('Scrum Area added successfully!', 'Close', { duration: 3000 });
          this.scrumAreaForm.reset();
          this.loadScrumAreas();
        },
        error: (err) =>{
          const err_msg=err.error.message[0].split(":")[1]?.trim() || 'Failed to add Scrum Area.'
        this.openDialog('Error', err_msg)
        }
      });
    }
  }

  editScrumArea(area: any) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;
    this.scrumAreaForm.patchValue(area);
    this.editingScrumAreaId = area.id;
  }

  deleteScrumArea(id: number) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        title: 'Delete Scrum Area',
        message: 'Are you sure you want to delete this Scrum Area?',
        buttonText: 'Delete'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.velocityService.deleteScrumArea(id).subscribe({
          next: () => {
            this.snackBar.open('Scrum Area deleted', 'Close', { duration: 3000 });
            this.loadScrumAreas();
            location.reload();
            if (this.editingScrumAreaId === id) {
              this.scrumAreaForm.reset();
              this.editingScrumAreaId = null;
            }
          },
          error: () => this.openDialog('Error', 'Failed to delete Scrum Area.')
        });
      }
    });
  }

  saveVelocity() {
    if (this.velocityForm.invalid || !this.isLoggedIn || this.userRole === 'VIEWER') {
      this.velocityForm.markAllAsTouched();
      this.openDialog('Validation Error', 'Please complete all Velocity fields.');
      return;
    }

    const velocityPayload = this.velocityForm.value;
    const fullScrumArea = this.scrumAreas.find(area => area.id === velocityPayload.scrumAreaId);

    if (!fullScrumArea) {
      this.openDialog('Error', 'Scrum Area is required.');
      return;
    }

    const finalPayload = {
      ...velocityPayload,
      scrumArea: fullScrumArea
    };
    delete finalPayload.scrumAreaId;

    if (this.editingVelocityId) {
      this.velocityService.updateVelocity(this.editingVelocityId, finalPayload).subscribe({
        next: () => {
          this.snackBar.open('Velocity updated successfully!', 'Close', { duration: 3000 });
          this.velocityForm.reset();
          this.editingVelocityId = null;
          this.loadVelocities();
        },
        error: (err) => {
          const msg = err?.error?.message?.[0]?.split(':')[1]?.trim() || 'Failed to update Velocity.';
          this.openDialog('Error', msg);
        }
      });
    } else {
      this.velocityService.addVelocity(fullScrumArea.id, finalPayload).subscribe({
        next: () => {
          this.snackBar.open('Velocity added successfully!', 'Close', { duration: 3000 });
          this.velocityForm.reset();
          this.loadVelocities();
        },
        error: (err) => {
          const msg = err?.error?.message?.[0]?.split(':')[1]?.trim() || 'Failed to add Velocity.';
          this.openDialog('Error', msg);
        }
      });
    }
  }

  editVelocity(v: any) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;
    this.velocityForm.patchValue({
      scrumAreaId: v.scrumArea?.id,
      sprintName: v.sprintName,
      velocity: v.velocity,
      sprintEndDate: v.sprintEndDate
    });
    this.editingVelocityId = v.id;
  }

  deleteVelocity(id: number) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        title: 'Delete Velocity',
        message: 'Are you sure you want to delete this Velocity entry?',
        buttonText: 'Delete'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.velocityService.deleteVelocity(id).subscribe({
          next: () => {
            this.snackBar.open('Velocity deleted successfully', 'Close', { duration: 3000 });
            this.loadVelocities();
          },
          error: () => this.openDialog('Error', 'Failed to delete Velocity.')
        });
      }
    });
  }

  get filteredVelocities() {
    if (!this.selectedScrumAreaId) return this.velocities;
    return this.velocities.filter(v => v.scrumArea?.id === this.selectedScrumAreaId);
  }
}
