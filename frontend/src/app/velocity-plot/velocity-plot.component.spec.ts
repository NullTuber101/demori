import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VelocityPlotComponent } from './velocity-plot.component';
import { VelocityService } from '../services/velocity.service';
import { of } from 'rxjs';
import { BaseChartDirective } from 'ng2-charts';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('VelocityPlotComponent', () => {
  let component: VelocityPlotComponent;
  let fixture: ComponentFixture<VelocityPlotComponent>;
  let mockVelocityService: jasmine.SpyObj<VelocityService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('VelocityService', ['getVelocityChartData']);

    await TestBed.configureTestingModule({
      imports: [VelocityPlotComponent, HttpClientTestingModule],
      providers: [{ provide: VelocityService, useValue: spy }]
    }).compileComponents();

    mockVelocityService = TestBed.inject(VelocityService) as jasmine.SpyObj<VelocityService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VelocityPlotComponent);
    component = fixture.componentInstance;

    // Provide mock chart data
    mockVelocityService.getVelocityChartData.and.returnValue(
      of([
        {
          scrumAreaName: 'Area A',
          velocities: [
            { sprintEndDate: '2024-03-10', velocity: 40 },
            { sprintEndDate: '2024-02-28', velocity: 35 },
            { sprintEndDate: '2024-02-15', velocity: 45 }
          ]
        },
        {
          scrumAreaName: 'Area B',
          velocities: [
            { sprintEndDate: '2024-03-11', velocity: 50 },
            { sprintEndDate: '2024-02-20', velocity: 55 },
            { sprintEndDate: '2024-01-15', velocity: 60 }
          ]
        }
      ])
    );

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load and process chart data', () => {
    expect(component.barChartData).toBeTruthy();
    expect(component.barChartData?.labels?.length).toBe(2); // Area A & B
    expect(component.barChartData?.datasets?.length).toBe(4); // 3 sprints + average line
  });

  it('should call velocityService.getVelocityChartData once', () => {
    expect(mockVelocityService.getVelocityChartData).toHaveBeenCalledTimes(1);
  });
});
