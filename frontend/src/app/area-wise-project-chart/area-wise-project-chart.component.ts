import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AreaService } from '../services/area.service';
import { ProjectService } from '../services/project.service';
import { StatusService } from '../services/status.service';
import { Project } from '../models/project.model';
import { Status } from '../models/status.model';
import { Area } from '../models/area.model';
import { AreaWithProjects } from '../models/area-with-projects.model';

import { Chart, ChartConfiguration, ChartData, ChartType, registerables } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { BaseChartDirective } from 'ng2-charts';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { forkJoin } from 'rxjs';

Chart.register(...registerables, ChartDataLabels);

@Component({
  selector: 'app-area-wise-project-chart',
  standalone: true,
  templateUrl: './area-wise-project-chart.component.html',
  styleUrls: ['./area-wise-project-chart.component.css'],
  providers: [AreaService, ProjectService, StatusService],
  imports: [
    CommonModule,
    BaseChartDirective,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class AreaWiseProjectChartComponent implements OnInit {
  @Output() loaded = new EventEmitter<void>();
  @Output() error = new EventEmitter<string>();

  areas: AreaWithProjects[] = [];
  projects: Project[] = [];
  statuses: Status[] = [];
  currentAreaIndex = 0;

  barChartType: ChartType = 'bar';
  ChartDataLabels = ChartDataLabels;

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    scales: {
      x: { beginAtZero: true, max: 100, ticks: { padding: 5 } },
      y: {
        beginAtZero: true,
        ticks: { autoSkip: false, font: { size: 12 } },
        title: { display: true, text: 'Projects' }
      }
    },
    layout: { padding: { right: 75 } },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value >= 95 ? 'start' : 'right';
        },
        clamp: true,
        clip: false,
        formatter: (_value, context) => {
          if (!this.hasProjects()) return '';
          const project = this.areas[this.currentAreaIndex].projects[context.dataIndex];
          return project?.status?.statusName || '';
        },
        color: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value > 75 ? 'white' : 'black';
        },
        font: { weight: 'bold', size: 12 },
        backgroundColor: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value > 75 ? 'rgba(0,0,0,0.5)' : null;
        },
        borderRadius: 2,
        padding: { top: 2, bottom: 2, left: 4, right: 4 }
      },
      legend: { display: false },
      tooltip: {
        enabled: true,
        titleFont: { size: 14 },
        bodyFont: { size: 13 }
      }
    },
    datasets: {
      bar: {
        barThickness: 25,
        maxBarThickness: 30,
        barPercentage: 0.9,
        categoryPercentage: 0.9
      }
    }
  };

  constructor(
    private areaService: AreaService,
    private projectService: ProjectService,
    private statusService: StatusService
  ) {}

  ngOnInit(): void {
    forkJoin({
      areas: this.areaService.getAreas(),
      projects: this.projectService.getAllProjects(),
      statuses: this.statusService.getStatuses()
    }).subscribe({
      next: ({ areas, projects, statuses }) => {
        this.areas = (areas as Area[]).map(area => ({ ...area, projects: [] }));
        this.projects = projects;
        this.statuses = statuses;

        if (this.areas.length > 0) this.currentAreaIndex = 0;
        this.groupProjectsByArea();

        this.loaded.emit(); 
        console.log('[AreaWiseProjectChart] emitted loaded');
      },
      error: (err) => {
        console.error('Area chart load error:', err);
        this.error.emit('Failed to load area-wise project chart.');
      }
    });
  }

  groupProjectsByArea(): void {
    this.areas.forEach(area => {
      const areaProjects = this.projects.filter(p => p.area?.id === area.id);
      area.projects = areaProjects.sort((a, b) => b.status.percentage - a.status.percentage);
    });
  }

  getChartData(projects?: Project[]): ChartData {
    if (!projects?.length) {
      return {
        labels: [],
        datasets: [{
          data: [],
          backgroundColor: [],
          borderColor: [],
          borderWidth: 1
        }]
      };
    }

    const data = projects.map(p => p.status.percentage >= 100 ? 99.5 : p.status.percentage);
    const colors = projects.map(p => this.getColor(p.status.percentage));

    return {
      labels: projects.map(p => p.projectName),
      datasets: [{
        data,
        backgroundColor: colors,
        borderColor: colors,
        borderWidth: 1
      }]
    };
  }

  getColor(value: number): string {
    if (value < 10) return '#00BFFF';
    if (value < 20) return '#00FA9A';
    if (value < 30) return '#7CFC00';
    if (value < 40) return '#32CD32';
    if (value < 50) return '#40E0D0';
    if (value < 60) return '#1E90FF';
    if (value < 70) return '#00CED1';
    if (value < 80) return '#20B2AA';
    if (value < 90) return '#3CB371';
    return '#4682B4';
  }

  hasProjects(): boolean {
  return !!(
    this.areas.length &&
    this.currentAreaIndex < this.areas.length &&
    this.areas[this.currentAreaIndex].projects?.length
  );
}


  getChartContainerStyle(): any {
    if (!this.hasProjects()) return { height: '300px' };
    const count = this.areas[this.currentAreaIndex].projects.length;
    return { height: `${Math.max(300, count * 40 + 50)}px` };
  }

  nextArea() {
    if (this.areas.length) this.currentAreaIndex = (this.currentAreaIndex + 1) % this.areas.length;
  }

  prevArea() {
    if (this.areas.length)
      this.currentAreaIndex = (this.currentAreaIndex - 1 + this.areas.length) % this.areas.length;
  }

  getCurrentAreaName(): string {
    return this.areas?.[this.currentAreaIndex]?.name || 'No area selected';
  }
}
