import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { StatusService } from '../services/status.service';
import { GenericDialogComponent } from '../generic-dialog/generic-dialog.component';
import { AlertComponent } from '../alert/alert.component';

@Component({
  selector: 'app-status-manager',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  templateUrl: './status-manager.component.html',
  styleUrls: ['./status-manager.component.css']
})
export class StatusManagerComponent {
  statuses: any[] = [];
  statusForm: FormGroup;
  editForm: FormGroup;
  editIndex: number | null = null;
  deleteIndex: number | null = null;
  sortAscending: boolean = true;

  isLoggedIn: boolean = false;
  userRole: string = 'VIEWER';

  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private statusService = inject(StatusService);

  constructor(private fb: FormBuilder) {
    this.statusForm = this.fb.group({
      statusName: ['', Validators.required],
      percentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    this.editForm = this.fb.group({
      id: [null],
      statusName: ['', Validators.required],
      percentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    this.loadStatuses();

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

  loadStatuses() {
    this.statusService.getStatuses().subscribe(statuses => {
      this.statuses = statuses;
      this.sortStatuses();
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

  addStatus() {
    if (this.statusForm.valid && this.isLoggedIn && this.userRole !== 'VIEWER') {
      this.statusService.addStatus(this.statusForm.value).subscribe({
        next: (newStatus) => {
          this.statuses.push(newStatus);
          this.sortStatuses();
          this.statusForm.reset();
          this.openAlert('success', 'Success', 'Status added successfully!');
        },
        error: (err) => {
          const message = err.error.message[0].split(":")[1]?.trim() || "";
          this.openAlert('error', 'Error', message);
        }
      });
    } else {
      this.openAlert('error', 'Validation Error', 'Please fill all required fields correctly.');
    }
  }

  enableEdit(index: number) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;
    this.editIndex = index;
    const status = this.statuses[index];
    this.editForm.setValue({
      id: status.id,
      statusName: status.statusName,
      percentage: status.percentage
    });
  }

  saveEdit(index: number) {
    if (this.editForm.valid && this.isLoggedIn && this.userRole !== 'VIEWER') {
      this.statusService.updateStatus(this.editForm.value).subscribe({
        next: (updatedStatus) => {
          this.statuses[index] = updatedStatus;
          this.sortStatuses();
          this.editIndex = null;
          this.openAlert('success', 'Success', 'Status updated successfully!');
        },
        error: (err) => {
          const message = err.error.message[0].split(":")[1]?.trim() || "Failed to update status. Please try again.";
          this.openAlert('error', 'Update Failed', message);
        }
      });
    } else {
      this.openAlert('error', 'Validation Error', 'Please fix the errors before saving.');
    }
  }

  cancelEdit() {
    this.editIndex = null;
  }

  confirmDelete(index: number) {
    if (!this.isLoggedIn || this.userRole === 'VIEWER') return;

    this.deleteIndex = index;
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        title: 'Confirmation',
        message: 'Are you sure you want to delete this status?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result === true) {
        this.statusService.deleteStatus(this.statuses[index].id).subscribe(() => {
          this.statuses.splice(index, 1);
          this.sortStatuses();
          this.openAlert('success', 'Deleted', 'Status deleted successfully!');
        });
      }
      this.deleteIndex = null;
    });
  }

  sortStatuses() {
    this.statuses.sort((a, b) => {
      return this.sortAscending ? a.percentage - b.percentage : b.percentage - a.percentage;
    });
  }

  toggleSortOrder() {
    this.sortAscending = !this.sortAscending;
    this.sortStatuses();
  }
}