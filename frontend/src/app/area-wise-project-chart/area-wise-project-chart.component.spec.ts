import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AreaWiseProjectChartComponent } from './area-wise-project-chart.component';
import { AreaService } from '../services/area.service';
import { ProjectService } from '../services/project.service';
import { StatusService } from '../services/status.service';
import { of } from 'rxjs';
import { AreaWithProjects } from '../models/area-with-projects.model';
import { Project } from '../models/project.model';
import { Status } from '../models/status.model';

describe('AreaWiseProjectChartComponent', () => {
  let component: AreaWiseProjectChartComponent;
  let fixture: ComponentFixture<AreaWiseProjectChartComponent>;

  const mockAreas: AreaWithProjects[] = [
    {
      id: 1,
      name: 'Area A',
      leadName: 'Lead A',
      leadEmail: 'lead.a@example.com',
      projects: []
    },
    {
      id: 2,
      name: 'Area B',
      leadName: 'Lead B',
      leadEmail: 'lead.b@example.com',
      projects: []
    }
  ];

  const mockProjects: Project[] = [
    {
      id: 101,
      projectName: 'Project 1',
      description: 'Test Desc 1',
      developer: 'Dev A',
      jira: 'JIRA-001',
      startDate: '2023-01-01',
      endDate: '2023-06-01',
      area: {
        id: 1,
        name: 'Area A',
        leadName: 'Lead A',
        leadEmail: 'lead.a@example.com'
      },
      status: {
        id: 1,
        statusName: 'In Progress',
        percentage: 60
      }
    },
    {
      id: 102,
      projectName: 'Project 2',
      description: 'Test Desc 2',
      developer: 'Dev B',
      jira: 'JIRA-002',
      startDate: '2023-02-01',
      endDate: '2023-07-01',
      area: {
        id: 2,
        name: 'Area B',
        leadName: 'Lead B',
        leadEmail: 'lead.b@example.com'
      },
      status: {
        id: 2,
        statusName: 'Done',
        percentage: 100
      }
    }
  ];

  const mockStatuses: Status[] = [
    { id: 1, statusName: 'In Progress', percentage: 60 },
    { id: 2, statusName: 'Done', percentage: 100 }
  ];

  const areaServiceSpy = jasmine.createSpyObj('AreaService', ['getAreas']);
  const projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects']);
  const statusServiceSpy = jasmine.createSpyObj('StatusService', ['getStatuses']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AreaWiseProjectChartComponent],
      providers: [
        { provide: AreaService, useValue: areaServiceSpy },
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: StatusService, useValue: statusServiceSpy }
      ]
    }).compileComponents();

    areaServiceSpy.getAreas.and.returnValue(of(mockAreas));
    projectServiceSpy.getAllProjects.and.returnValue(of(mockProjects));
    statusServiceSpy.getStatuses.and.returnValue(of(mockStatuses));

    fixture = TestBed.createComponent(AreaWiseProjectChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load areas and set current index to 0', () => {
    expect(component.areas.length).toBeGreaterThan(0);
    expect(component.currentAreaIndex).toBe(0);
  });

  it('should increment currentAreaIndex in nextArea()', () => {
    component.nextArea();
    expect(component.currentAreaIndex).toBe(1);
  });

  it('should decrement currentAreaIndex in prevArea()', () => {
    component.currentAreaIndex = 1;
    component.prevArea();
    expect(component.currentAreaIndex).toBe(0);
  });

  it('should return false for hasProjects() when no projects exist', () => {
    expect(component.hasProjects()).toBeFalse();
  });

  it('should return area name for getCurrentAreaName()', () => {
    expect(component.getCurrentAreaName()).toBe('Area A');
  });

  it('should return chart data with labels and values', () => {
    const chartData = component.getChartData(mockProjects);
    expect(chartData.labels?.length).toBe(2);
    expect(chartData.datasets[0].data.length).toBe(2);
  });

  it('should return empty chart data for undefined input', () => {
    const chartData = component.getChartData(undefined);
    expect(chartData.labels).toEqual([]);
    expect(chartData.datasets[0].data).toEqual([]);
  });
});
