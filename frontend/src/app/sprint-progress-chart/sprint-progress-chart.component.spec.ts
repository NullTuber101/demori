import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SprintProgressChartComponent } from './sprint-progress-chart.component';
import { BaseChartDirective } from 'ng2-charts';
import { By } from '@angular/platform-browser';

describe('SprintProgressChartComponent', () => {
  let component: SprintProgressChartComponent;
  let fixture: ComponentFixture<SprintProgressChartComponent>;

  const mockSprints = [
    {
      sprintStartDate: '2024-05-01',
      sprintFor: { statusName: 'Not Started', percentage: 0 }
    },
    {
      sprintStartDate: '2024-05-15',
      sprintFor: { statusName: 'In Progress', percentage: 50 }
    },
    {
      sprintStartDate: '2024-05-30',
      sprintFor: { statusName: 'Completed', percentage: 100 }
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SprintProgressChartComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SprintProgressChartComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize chart data when sprints are passed', () => {
    component.sprints = mockSprints;
    component.ngOnChanges();

    expect(component.lineChartData.labels?.length).toBe(3);
    expect(component.lineChartData.datasets[0].data).toEqual([0, 50, 100]);
    expect(component.lineChartData.datasets[0].label).toBe('Sprint Progress');
  });

  it('should render canvas element when sprints are present', () => {
    component.sprints = mockSprints;
    component.ngOnChanges();
    fixture.detectChanges();

    const canvas = fixture.debugElement.query(By.css('canvas'));
    expect(canvas).toBeTruthy();
  });

  it('should not render canvas if sprints array is empty', () => {
    component.sprints = [];
    component.ngOnChanges();
    fixture.detectChanges();

    const canvas = fixture.debugElement.query(By.css('canvas'));
    expect(canvas).toBeFalsy();
  });
});