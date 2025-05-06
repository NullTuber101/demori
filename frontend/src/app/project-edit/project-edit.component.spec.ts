import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectEditComponent } from './project-edit.component';
import { ProjectService } from '../services/project.service';
import { AreaService } from '../services/area.service';
import { StatusService } from '../services/status.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';

describe('ProjectEditComponent', () => {
  let component: ProjectEditComponent;
  let fixture: ComponentFixture<ProjectEditComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let areaServiceSpy: jasmine.SpyObj<AreaService>;
  let statusServiceSpy: jasmine.SpyObj<StatusService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getProjectById', 'editProject']);
    areaServiceSpy = jasmine.createSpyObj('AreaService', ['getAreas']);
    statusServiceSpy = jasmine.createSpyObj('StatusService', ['getStatuses']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ProjectEditComponent, MatSnackBarModule, MatDialogModule, ReactiveFormsModule],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: AreaService, useValue: areaServiceSpy },
        { provide: StatusService, useValue: statusServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '101' // projectId
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should decode user role from token', () => {
    const token = createMockJWT({ role: 'EDITOR' });
    localStorage.setItem('token', token);
    component.ngOnInit();
    expect(component.userRole).toBe('EDITOR');
  });

  it('should fallback to VIEWER on invalid token', () => {
    localStorage.setItem('token', 'invalid.token.payload');
    component.ngOnInit();
    expect(component.userRole).toBe('VIEWER');
  });

  it('should load project details and populate form', fakeAsync(() => {
    const mockProject = {
      id: 101,
      projectName: 'Test Project',
      description: 'Test',
      developer: 'Dev',
      jira: 'JIRA-1',
      startDate: '2023-01-01',
      endDate: '2023-02-01',
      area: {
        id: 1,
        name: 'Area 1',
        leadName: 'Alice',
        leadEmail: 'alice@example.com'
      },
      status: {
        id: 2,
        statusName: 'Completed',
        percentage: 100
      }
    };

    projectServiceSpy.getProjectById.and.returnValue(of(mockProject));
    component.loadProjectDetails();
    tick();

    expect(component.projectForm.value.projectName).toBe('Test Project');
  }));

  it('should show alert on project load failure', fakeAsync(() => {
    projectServiceSpy.getProjectById.and.returnValue(throwError(() => ({})));
    component.loadProjectDetails();
    tick();
    expect(dialogSpy.open).toHaveBeenCalled();
  }));

  it('should call editProject on valid form submit', fakeAsync(() => {
    component.projectId = '101';
    component.isLoggedIn = true;
    component.userRole = 'EDITOR';
    component.projectForm.setValue({
      areaId: 1,
      projectName: 'Updated',
      description: '',
      developer: 'Dev',
      jira: '',
      startDate: new Date(),
      endDate: new Date(),
      statusId: 2
    });

    projectServiceSpy.editProject.and.returnValue(of({}));
    component.onSubmit();
    tick();

    expect(projectServiceSpy.editProject).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  }));

  it('should show alert on update error', fakeAsync(() => {
    component.projectForm.setValue({
      areaId: 1,
      projectName: 'Failing',
      description: '',
      developer: 'Dev',
      jira: '',
      startDate: new Date(),
      endDate: new Date(),
      statusId: 2
    });

    projectServiceSpy.editProject.and.returnValue(
      throwError(() => ({
        error: { message: ['error: Update failed'] }
      }))
    );

    component.onSubmit();
    tick();
    expect(dialogSpy.open).toHaveBeenCalled();
  }));

  it('should show alert on invalid form submission', () => {
    component.projectForm.patchValue({ projectName: '' });
    component.onSubmit();
    expect(projectServiceSpy.editProject).not.toHaveBeenCalled();
    expect(dialogSpy.open).toHaveBeenCalled();
  });

  function createMockJWT(payload: object): string {
    const base64Payload = btoa(JSON.stringify(payload));
    return `header.${base64Payload}.signature`;
  }
});
