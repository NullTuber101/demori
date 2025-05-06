import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface AlertData {
  title: string;
  message: string;
}

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: AlertData,
    private dialogRef: MatDialogRef<AlertComponent>
  ) {}

  close() {
    this.dialogRef.close();
  }
}
