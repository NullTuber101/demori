import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AreaService } from '../services/area.service';
import { ProjectService } from '../services/project.service';
import { StatusService } from '../services/status.service';
import { Project } from '../models/project.model';
import { Status } from '../models/status.model';
import { AreaWithProjects } from '../models/area-with-projects.model';

import { Chart, ChartConfiguration, ChartData, ChartType, registerables } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { BaseChartDirective } from 'ng2-charts';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Register chart.js plugins
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
      x: {
        beginAtZero: true,
        max: 100,
        ticks: {
          padding: 5
        }
      },
      y: {
        beginAtZero: true,
        ticks: {
          autoSkip: false,
          font: {
            size: 12
          }
        },
        title: {
          display: true,
          text: 'Projects'
        }
      }
    },
    layout: {
      padding: {
        right: 75
      }
    },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value >= 95 ? 'start' : 'right'; // ✅ Smart label flip for wide bars
        },
        clamp: true,
        clip: false, // ✅ Prevent label from being cut off
        formatter: (_value, context) => {
          if (
            this.areas.length === 0 ||
            this.currentAreaIndex >= this.areas.length ||
            !this.areas[this.currentAreaIndex].projects ||
            context.dataIndex >= this.areas[this.currentAreaIndex].projects.length
          ) {
            return '';
          }

          const project = this.areas[this.currentAreaIndex].projects[context.dataIndex];
          return project && project.status ? project.status.statusName : '';
        },
        color: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value > 75 ? 'white' : 'black';
        },
        font: {
          weight: 'bold',
          size: 12
        },
        backgroundColor: (context) => {
          const value = context.dataset.data[context.dataIndex] as number;
          return value > 75 ? 'rgba(0,0,0,0.5)' : null;
        },
        borderRadius: 2,
        padding: {
          top: 2,
          bottom: 2,
          left: 4,
          right: 4
        }
      },
      legend: { display: false },
      tooltip: {
        enabled: true,
        titleFont: {
          size: 14
        },
        bodyFont: {
          size: 13
        }
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
    this.areaService.getAreas().subscribe((areas) => {
      this.areas = areas as AreaWithProjects[];
      if (this.areas.length > 0) {
        this.currentAreaIndex = 0;
      }
    });

    this.projectService.getAllProjects().subscribe((projects) => {
      this.projects = projects;
       console.log('Fetched Projects:', this.projects);
      this.groupProjectsByArea();
    });

    this.statusService.getStatuses().subscribe((statuses) => {
      this.statuses = statuses;
      console.log('Fetched Statuses:', this.statuses);
    });
  }

groupProjectsByArea(): void {
  if (this.areas && this.projects) {
    this.areas.forEach(area => {
      const areaProjects = this.projects.filter(project =>
        project.area && project.area.id === area.id
      );

      // Sort the projects by status percentage descending
      area.projects = areaProjects.sort((a, b) => b.status.percentage - a.status.percentage);
    });
  }
}


  getChartData(projects: Project[] | undefined): ChartData {
    if (!projects || projects.length === 0) {
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

    const data = projects.map(project =>
      project.status.percentage >= 100 ? 99.5 : project.status.percentage
    );

    const colors = projects.map(project =>
      this.getColor(project.status.percentage)
    );

    return {
      labels: this.getProjectNames(projects),
      datasets: [{
        data,
        backgroundColor: colors,
        borderColor: colors,
        borderWidth: 1
      }]
    };
  }

  getProjectNames(projects: Project[]): string[] {
    return projects.map(project => project.projectName);
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

  nextArea(): void {
    if (this.areas && this.areas.length > 0) {
      this.currentAreaIndex = (this.currentAreaIndex + 1) % this.areas.length;
    }
  }

  prevArea(): void {
    if (this.areas && this.areas.length > 0) {
      this.currentAreaIndex = (this.currentAreaIndex - 1 + this.areas.length) % this.areas.length;
    }
  }

  hasProjects(): boolean {
    return this.areas.length > 0 &&
           this.currentAreaIndex < this.areas.length &&
           !!this.areas[this.currentAreaIndex]?.projects?.length;
  }

  getCurrentAreaName(): string {
    return this.areas.length > 0 &&
           this.currentAreaIndex < this.areas.length &&
           !!this.areas[this.currentAreaIndex] ?
           this.areas[this.currentAreaIndex].name : 'No area selected';
  }

  getChartContainerStyle(): any {
    if (!this.hasProjects()) {
      return { height: '300px' };
    }

    const projectCount = this.areas[this.currentAreaIndex].projects.length;
    const calculatedHeight = Math.max(300, (projectCount * 40) + 50);

    return {
      height: `${calculatedHeight}px`,
      minHeight: '300px'
    };
  }
}
