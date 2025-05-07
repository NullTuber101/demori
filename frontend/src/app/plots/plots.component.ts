import { Component, inject } from '@angular/core';
import { AreaWiseProjectChartComponent } from '../area-wise-project-chart/area-wise-project-chart.component';
import { VelocityPlotComponent } from '../velocity-plot/velocity-plot.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AlertComponent } from '../alert/alert.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-plots',
  standalone: true,
  templateUrl: './plots.component.html',
  styleUrls: ['./plots.component.css'],
  imports: [
    CommonModule,
    AreaWiseProjectChartComponent,
    VelocityPlotComponent,
    MatProgressSpinnerModule
  ]
})
export class PlotsComponent {
  loading = true;
  private loadedCount = 0;

  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  onChildLoaded() {
  this.loadedCount++;
  console.log(`Loaded child count: ${this.loadedCount}`);
  if (this.loadedCount === 2) {
    this.loading = false;
  }
}


  onChildError(message: string) {
  console.error('Child component failed to load:', message);
  this.loading = false;
  this.openAlert('error', 'Chart Load Failed', message);
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
}
