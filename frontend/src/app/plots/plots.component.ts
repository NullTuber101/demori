import { Component } from '@angular/core';
import { AreaWiseProjectChartComponent } from '../area-wise-project-chart/area-wise-project-chart.component';
import { VelocityPlotComponent } from '../velocity-plot/velocity-plot.component';

@Component({
  selector: 'app-plots',
  standalone:true,
  imports: [AreaWiseProjectChartComponent,VelocityPlotComponent],
  templateUrl: './plots.component.html',
  styleUrl: './plots.component.css'
})
export class PlotsComponent {

}
