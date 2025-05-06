import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AdminService } from '../services/admin.service';
import { AlertComponent } from '../alert/alert.component';
import { GenericDialogComponent } from '../generic-dialog/generic-dialog.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatSelectModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  providers: [AdminService]
})
export class AdminDashboardComponent implements OnInit {
  pendingRequests = signal<any[]>([]);
  approvedUsers = signal<any[]>([]);
  availableRoles = ['SUPER_USER', 'EDITOR', 'VIEWER'];

  private adminService = inject(AdminService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  ngOnInit(): void {
    this.fetchRequests();
    this.fetchApprovedUsers();
  }

  fetchRequests() {
    this.adminService.getPendingRequests().subscribe({
      next: (res) => this.pendingRequests.set(res),
      error: () => this.openAlert('error', 'Load Error', 'Failed to fetch pending requests.')
    });
  }

  fetchApprovedUsers() {
    this.adminService.getApprovedUsers().subscribe({
      next: (res) => {
        const usersWithFlags = res.map(u => ({
          ...u,
          editing: false,
          pendingRole: u.role?.roleName || u.role
        }));
        this.approvedUsers.set(usersWithFlags);
      },
      error: () => this.openAlert('error', 'Load Error', 'Failed to fetch approved users.')
    });
  }

  approve(id: number, role: string) {
  const dialogRef = this.dialog.open(GenericDialogComponent, {
    data: {
      title: 'Approve User',
      message: `Are you sure you want to approve this user with role "${role}"?`,
      confirmText: 'Approve',
      cancelText: 'Cancel'
    }
  });

  dialogRef.afterClosed().subscribe((confirmed: boolean) => {
    if (confirmed) {
      this.adminService.approveRequest(id, role).subscribe({
        next: (res) => {
          this.openAlert('success', 'Approved', res.message || 'User approved successfully.');
          this.fetchRequests();
          this.fetchApprovedUsers();
        },
        error: (err) => {
          const msg = err.error?.error || 'Approval failed.';
          this.openAlert('error', 'Approval Error', msg);
        }
      });
    }
  });
}


  reject(id: number) {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        title: 'Reject User',
        message: 'Are you sure you want to reject this request?',
        confirmText: 'Reject',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.rejectRequest(id, 'Rejected by admin').subscribe({
          next: () => {
            this.openAlert('success', 'Rejected', 'User rejected successfully.');
            this.fetchRequests();
          },
          error: () => this.openAlert('error', 'Rejection Error', 'Failed to reject user.')
        });
      }
    });
  }

  submitRoleUpdate(index: number) {
  const users = this.approvedUsers();
  const user = users[index];

  const dialogRef = this.dialog.open(GenericDialogComponent, {
    data: {
      title: 'Confirm Role Change',
      message: `Are you sure you want to change this user's role to "${user.pendingRole}"?`,
      confirmText: 'Update',
      cancelText: 'Cancel'
    }
  });

  dialogRef.afterClosed().subscribe((confirmed: boolean) => {
    if (confirmed) {
      this.adminService.updateUserRole(user.id, user.pendingRole).subscribe({
        next: () => {
          this.openAlert('success', 'Updated', 'Role updated successfully.');
          user.role = user.pendingRole;
          user.editing = false;
          this.approvedUsers.set([...users]);
        },
        error: () => {
          this.openAlert('error', 'Update Error', 'Failed to update role.');
        }
      });
    } else {
      user.pendingRole = user.role?.roleName || user.role;
      user.editing = false;
      this.approvedUsers.set([...users]);
    }
  });
}

  updatePendingRole(index: number, newRole: string) {
    const users = this.approvedUsers();
    users[index].pendingRole = newRole;
    this.approvedUsers.set([...users]);
  }

  enableEdit(index: number) {
    const users = this.approvedUsers();
    users[index].editing = true;
    users[index].pendingRole = users[index].role?.roleName || users[index].role;
    this.approvedUsers.set([...users]);
  }

  downgradeToViewer(userId: number) {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        title: 'Downgrade to Viewer',
        message: 'Are you sure you want to downgrade this user to VIEWER?',
        confirmText: 'Downgrade',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.updateRole(userId, 'VIEWER');
      }
    });
  }

  updateRole(userId: number, newRole: string) {
    this.adminService.updateUserRole(userId, newRole).subscribe({
      next: () => {
        this.openAlert('success', 'Updated', 'Role updated successfully.');
        this.fetchApprovedUsers();
      },
      error: () => {
        this.openAlert('error', 'Update Error', 'Error updating role.');
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
        panelClass: ['snackbar-success']
      });
    }
  }
}
