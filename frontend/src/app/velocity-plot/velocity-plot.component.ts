import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { VelocityService } from '../services/velocity.service';
import { Chart, ChartData, ChartOptions, registerables } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule } from '@angular/common';

Chart.register(...registerables, ChartDataLabels);

@Component({
  selector: 'app-velocity-plot',
  standalone: true,
  imports: [CommonModule, HttpClientModule, BaseChartDirective],
  templateUrl: './velocity-plot.component.html',
  styleUrls: ['./velocity-plot.component.css']
})
export class VelocityPlotComponent implements OnInit {
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  ChartDataLabels = ChartDataLabels;
  barChartData: ChartData<'bar' | 'line'> | undefined;

  chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' },
      tooltip: { mode: 'index', intersect: false },
      datalabels: {
        display: (ctx: any) => ctx.dataset.label === 'Average Velocity',
        color: '#007bff',
        font: {
          size: 12,
          weight: 'bold'
        },
        anchor: 'end',
        align: 'start',
        offset: 6,
        formatter: (value: any) => {
          return value && value !== 0 ? value.toFixed(2) : '';
        }
      }
    },
    scales: {
      x: {
        ticks: { font: { size: 12 } },
        title: { display: true, text: 'Scrum Area', font: { size: 14 } }
      },
      y: {
        beginAtZero: true,
        ticks: { font: { size: 12 }, stepSize: 10 },
        title: { display: true, text: 'Velocity', font: { size: 14 } }
      },
      yLine: {                   // 🔥 New layer for line chart
        position: 'left',
        display: false,
        min: 0
      }
    }
  };

  constructor(private velocityService: VelocityService) {}

  ngOnInit(): void {
    this.velocityService.getVelocityChartData().subscribe(data => {
      this.prepareChartData(data);
    });
  }

  prepareChartData(data: any[]): void {
    const labels: string[] = [];
    const sprintMap = new Map<string, number[]>();
    const sprintLabels = ['Sprint 1', 'Sprint 2', 'Sprint 3'];
    sprintLabels.forEach(label => sprintMap.set(label, []));

    data.forEach((area: any) => {
      labels.push(area.scrumAreaName);

      const sortedSprints = area.velocities
        .sort((a: any, b: any) => new Date(b.sprintEndDate).getTime() - new Date(a.sprintEndDate).getTime())
        .slice(0, 3);

      sortedSprints.forEach((v: any, i: number) => {
        sprintMap.get(sprintLabels[i])?.push(v.velocity ?? 0);
      });

      // for (let i = sortedSprints.length; i < 3; i++) {
      //   sprintMap.get(sprintLabels[i])?.push(0);
      // }
    });

    const avgVelocities = labels.map((_, i) => {
      let total = 0;
      let count = 0;
      sprintMap.forEach(arr => {
        const val = arr[i];
        if (val !== undefined) {
          total += val;
          count++;
        }
      });
      return count ===0 ? 0: +(total / count).toFixed(2);
    });

    const sprintColors = ['#b09cc8', '#66BB6A', '#FFA726'];

    const datasets: ChartData<'bar' | 'line'>['datasets'] = sprintLabels.map((label, i) => ({
      label,
      data: sprintMap.get(label) ?? [],
      type: 'bar',
      order: 1,
      borderSkipped: false,
      backgroundColor: sprintColors[i],
      borderColor: '#fff',
      borderWidth: 1,
      barThickness: 40,
      categoryPercentage: 0.6,
      barPercentage: 0.95
    }));

   datasets.push({
    label: 'Average Velocity',
    data: avgVelocities,
    type: 'line',
    order: 99,
    yAxisID: 'yLine',
    clip: false,
    borderColor: '#007bff',
    backgroundColor: '#007bff33',
    tension: 0.4,
    fill: false,
    pointRadius: 6,                // bigger for laser feel
    pointHoverRadius: 8,
    pointBackgroundColor: '#000',  // laser color
    pointBorderColor: '#fff',      // white outer ring
    pointBorderWidth: 2
  });


    this.barChartData = {
      labels,
      datasets
    };

    this.chart?.update();
  }
}
