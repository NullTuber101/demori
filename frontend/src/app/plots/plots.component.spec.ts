import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PlotsComponent } from './plots.component';
import { AreaWiseProjectChartComponent } from '../area-wise-project-chart/area-wise-project-chart.component';
import { VelocityPlotComponent } from '../velocity-plot/velocity-plot.component';

describe('PlotsComponent', () => {
  let component: PlotsComponent;
  let fixture: ComponentFixture<PlotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        PlotsComponent,
        AreaWiseProjectChartComponent,
        VelocityPlotComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PlotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render AreaWiseProjectChartComponent and VelocityPlotComponent', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-area-wise-project-chart')).toBeTruthy();
    expect(compiled.querySelector('app-velocity-plot')).toBeTruthy();
  });

  it('should display section titles', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const headings = compiled.querySelectorAll('h3.section-caption');
    expect(headings.length).toBe(2);
    expect(headings[0].textContent).toContain('Project Status by Area');
    expect(headings[1].textContent).toContain('Scrum-Wise Velocity');
  });
});
