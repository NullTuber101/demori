import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectAddComponent } from './project-add.component';
import { ProjectService } from '../services/project.service';
import { of, throwError } from 'rxjs';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';

describe('ProjectAddComponent', () => {
  let component: ProjectAddComponent;
  let fixture: ComponentFixture<ProjectAddComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAreas', 'getStatuses', 'addProject']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ProjectAddComponent, MatSnackBarModule, MatDialogModule, ReactiveFormsModule],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should decode role as SUPER_USER from token', () => {
    const token = createMockJWT({ role: 'SUPER_USER' });
    localStorage.setItem('token', token);
    component.ngOnInit();
    expect(component.userRole).toBe('SUPER_USER');
  });

  it('should fallback to VIEWER on invalid token', () => {
    localStorage.setItem('token', 'invalid.token.payload');
    component.ngOnInit();
    expect(component.userRole).toBe('VIEWER');
  });

  it('should load areas and statuses on init', () => {
    const mockAreas = [{ id: 1, name: 'Area A' }];
    const mockStatuses = [{ id: 1, statusName: 'Done' }];
    projectServiceSpy.getAreas.and.returnValue(of(mockAreas));
    projectServiceSpy.getStatuses.and.returnValue(of(mockStatuses));

    component.ngOnInit();
    expect(projectServiceSpy.getAreas).toHaveBeenCalled();
    expect(projectServiceSpy.getStatuses).toHaveBeenCalled();
  });

  it('should submit form successfully', fakeAsync(() => {
    component.isLoggedIn = true;
    component.userRole = 'EDITOR';
    component.projectForm.setValue({
      areaId: 1,
      projectName: 'Test Project',
      description: 'Test Desc',
      developer: 'Dev Name',
      jira: 'JIRA-123',
      startDate: '2023-01-01',
      endDate: '2023-02-01',
      statusId: 1
    });

    projectServiceSpy.addProject.and.returnValue(of({}));
    component.onSubmit();
    tick();

    expect(projectServiceSpy.addProject).toHaveBeenCalled();
  }));

  it('should show alert on invalid form submission', () => {
    component.projectForm.patchValue({ projectName: '' }); // mark it invalid
    component.onSubmit();
    expect(projectServiceSpy.addProject).not.toHaveBeenCalled();
    expect(dialogSpy.open).toHaveBeenCalled(); // Validation alert
  });

  it('should show alert on API error during submit', fakeAsync(() => {
    component.projectForm.setValue({
      areaId: 1,
      projectName: 'P1',
      description: '',
      developer: 'Dev',
      jira: '',
      startDate: '2023-01-01',
      endDate: '2023-02-01',
      statusId: 1
    });

    projectServiceSpy.addProject.and.returnValue(
      throwError(() => ({
        error: { message: ['error: Invalid project data'] }
      }))
    );

    component.onSubmit();
    tick();

    expect(dialogSpy.open).toHaveBeenCalled();
  }));

  function createMockJWT(payload: object): string {
    const base64Payload = btoa(JSON.stringify(payload));
    return `header.${base64Payload}.signature`;
  }
});
