import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { VelocitySectionComponent } from './velocity-section.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { VelocityService } from '../services/velocity.service';
import { of, throwError } from 'rxjs';

// Mock VelocityService
class MockVelocityService {
  getScrumAreas = jasmine.createSpy().and.returnValue(of([]));
  getVelocities = jasmine.createSpy().and.returnValue(of([]));
  addScrumArea = jasmine.createSpy().and.returnValue(of({}));
  updateScrumArea = jasmine.createSpy().and.returnValue(of({}));
  deleteScrumArea = jasmine.createSpy().and.returnValue(of({}));
  addVelocity = jasmine.createSpy().and.returnValue(of({}));
  updateVelocity = jasmine.createSpy().and.returnValue(of({}));
  deleteVelocity = jasmine.createSpy().and.returnValue(of({}));
}

describe('VelocitySectionComponent', () => {
  let component: VelocitySectionComponent;
  let fixture: ComponentFixture<VelocitySectionComponent>;
  let service: MockVelocityService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        VelocitySectionComponent,
        ReactiveFormsModule,
        FormsModule,
        MatSnackBarModule,
        MatDialogModule
      ],
      providers: [{ provide: VelocityService, useClass: MockVelocityService }]
    }).compileComponents();

    fixture = TestBed.createComponent(VelocitySectionComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(VelocityService) as any;

    localStorage.setItem('token',
      'eyJhbGciOiJIUzI1NiJ9.' +
      btoa(JSON.stringify({ role: 'SUPER_USER' })) +
      '.signature');

    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load scrum areas and velocities on init', () => {
    expect(service.getScrumAreas).toHaveBeenCalled();
    expect(service.getVelocities).toHaveBeenCalled();
  });

  it('should not save scrum area if form is invalid', () => {
    component.scrumAreaForm.reset();
    component.saveScrumArea();
    expect(service.addScrumArea).not.toHaveBeenCalled();
  });

  it('should add a scrum area if form is valid', () => {
    component.scrumAreaForm.setValue({
      areaName: 'Team A',
      scrumMaster: 'John',
      scrumTeam: 'Alpha',
      boardId: 'B1'
    });
    component.saveScrumArea();
    expect(service.addScrumArea).toHaveBeenCalled();
  });

  it('should add velocity if form is valid', () => {
    component.scrumAreas = [{ id: 1, areaName: 'A' }];
    component.velocityForm.setValue({
      sprintName: 'Sprint 1',
      velocity: 20,
      sprintEndDate: new Date(),
      scrumAreaId: 1
    });
    component.saveVelocity();
    expect(service.addVelocity).toHaveBeenCalled();
  });

  it('should not add velocity if scrumArea is missing', () => {
    component.velocityForm.setValue({
      sprintName: 'Sprint 1',
      velocity: 20,
      sprintEndDate: new Date(),
      scrumAreaId: 999
    });
    component.saveVelocity();
    expect(service.addVelocity).not.toHaveBeenCalled();
  });

  it('should enter edit mode for scrum area', () => {
    const area = { id: 5, areaName: 'A', scrumMaster: 'B', scrumTeam: 'C', boardId: 'D' };
    component.editScrumArea(area);
    expect(component.editingScrumAreaId).toBe(5);
  });

  it('should enter edit mode for velocity', () => {
    const velocity = {
      id: 2,
      scrumArea: { id: 1 },
      sprintName: 'Sprint X',
      velocity: 40,
      sprintEndDate: new Date()
    };
    component.editVelocity(velocity);
    expect(component.editingVelocityId).toBe(2);
  });
});
