import { Component, Input, OnChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  Chart,
  ChartConfiguration,
  registerables
} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import {
  BaseChartDirective,
  provideCharts,
  withDefaultRegisterables
} from 'ng2-charts';

// Register Chart.js core + datalabel plugin
Chart.register(...registerables, ChartDataLabels);

@Component({
  selector: 'app-sprint-progress-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  providers: [provideCharts(withDefaultRegisterables())],
  templateUrl: './sprint-progress-chart.component.html',
  styleUrls: ['./sprint-progress-chart.component.css']
})
export class SprintProgressChartComponent implements OnChanges {
  @Input() sprints: any[] = [];

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public lineChartType: 'line' = 'line';

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  public lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    layout: {
      padding: {
        top: 30 // ✅ Adds space above chart for labels
      }
    },
    plugins: {
      datalabels: {
        display: true,
        align: 'top',
        anchor: 'end',
        font: {
          weight: 'bold'
        },
        formatter: (_value, context) => {
          return this.pointLabels[context.dataIndex] ?? '';
        }
      },
      legend: {
        display: false
      }
    },
    scales: {
      x: {
        title: {
          display: true,
          text: 'Date'
        },
        offset: true, // ✅ Adds horizontal spacing
        ticks: {
          padding: 10
        }
      },
      y: {
        min: 0,
        max: 100,
        title: {
          display: true,
          text: 'Progress (%)'
        }
      }
    }
  };

  private pointLabels: string[] = [];

  ngOnChanges(): void {
    if (!this.sprints?.length) return;

    const sorted = [...this.sprints].sort((a, b) =>
      new Date(a.sprintStartDate).getTime() - new Date(b.sprintStartDate).getTime()
    );

    const labels = sorted.map(s =>
      new Date(s.sprintStartDate).toLocaleDateString()
    );

    const data = sorted.map(s => s.sprintFor?.percentage ?? 0);

    this.pointLabels = sorted.map(s => s.sprintFor?.statusName ?? '');

    this.lineChartData = {
      labels,
      datasets: [
        {
          data,
          label: 'Sprint Progress',
          borderColor: '#3f51b5',
          fill: false,
          tension: 0.3,
          pointRadius: 5,
          pointBackgroundColor: '#3f51b5',
          clip: false, // ✅ prevent clipping of start/end points
          datalabels: {
            align: 'top',
            anchor: 'end',
            formatter: (_value, context) =>
              this.pointLabels[context.dataIndex] ?? ''
          }
        }
      ]
    };

    this.chart?.update();
  }
}
