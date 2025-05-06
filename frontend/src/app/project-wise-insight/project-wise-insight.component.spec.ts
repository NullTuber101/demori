import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectWiseInsightComponent } from './project-wise-insight.component';
import { of, throwError } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

describe('ProjectWiseInsightComponent', () => {
  let component: ProjectWiseInsightComponent;
  let fixture: ComponentFixture<ProjectWiseInsightComponent>;
  let mockSprintService: any;
  let mockStatusService: any;
  let mockProjectService: any;
  let mockDialog: any;
  let mockSnackBar: any;

  beforeEach(async () => {
    mockSprintService = jasmine.createSpyObj(['getSprints', 'createSprint', 'updateSprint', 'deleteSprint']);
    mockStatusService = jasmine.createSpyObj(['getStatuses']);
    mockProjectService = jasmine.createSpyObj(['getProjectById']);
    mockDialog = jasmine.createSpyObj(['open']);
    mockSnackBar = jasmine.createSpyObj(['open']);

    await TestBed.configureTestingModule({
      imports: [ProjectWiseInsightComponent],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['id', '1']]) } } },
        { provide: MatDialog, useValue: mockDialog },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: 'SprintService', useValue: mockSprintService },
        { provide: 'StatusService', useValue: mockStatusService },
        { provide: 'ProjectService', useValue: mockProjectService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectWiseInsightComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load project name on init', () => {
    mockProjectService.getProjectById.and.returnValue(of({ projectName: 'Test Project' }));
    component.ngOnInit();
    expect(component.projectName).toBe('Test Project');
  });

  it('should handle sprint creation', () => {
    mockSprintService.createSprint.and.returnValue(of({ id: 1, sprintName: 'Sprint 1' }));
    component.isLoggedIn = true;
    component.userRole = 'EDITOR';
    component.sprintForm.setValue({
      sprintName: 'Sprint 1',
      sprintStartDate: new Date(),
      sprintEndDate: new Date(),
      sprintJira: 'JIRA-1',
      sprintDescription: 'Description',
      assignedTo: 'User',
      sprintFor: { id: 1 }
    });
    component.addSprint();
    expect(mockSprintService.createSprint).toHaveBeenCalled();
  });

  it('should open error dialog when sprint creation fails', () => {
    mockSprintService.createSprint.and.returnValue(throwError(() => ({
      error: { message: ['Validation: Something went wrong'] }
    })));
    component.isLoggedIn = true;
    component.userRole = 'EDITOR';
    component.sprintForm.setValue({
      sprintName: 'Sprint 1',
      sprintStartDate: new Date(),
      sprintEndDate: new Date(),
      sprintJira: 'JIRA-1',
      sprintDescription: 'Description',
      assignedTo: 'User',
      sprintFor: { id: 1 }
    });
    component.addSprint();
    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should enable and populate edit form', () => {
    component.sprints = [{ id: 1, sprintName: 'Sprint 1', sprintStartDate: new Date(), sprintEndDate: new Date(), sprintJira: 'JIRA', sprintDescription: 'desc', assignedTo: 'user', sprintFor: { id: 1 } }];
    component.isLoggedIn = true;
    component.userRole = 'EDITOR';
    component.enableEdit(0);
    expect(component.editIndex).toBe(0);
  });
});