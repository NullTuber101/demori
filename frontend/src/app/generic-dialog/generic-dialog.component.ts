import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-generic-dialog',
  standalone: true, // Make this a standalone component
  imports: [CommonModule, MatButtonModule, MatDialogModule], // Import necessary Angular Material modules
  templateUrl: './generic-dialog.component.html',
  styleUrls: ['./generic-dialog.component.css']
})
export class GenericDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<GenericDialogComponent>, // Reference to close the dialog
    @Inject(MAT_DIALOG_DATA) public data: any // Inject passed data (title, message, button text)
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true); // Return true on confirmation
  }

  onCancel(): void {
    this.dialogRef.close(false); // Return false on cancel
  }
}
