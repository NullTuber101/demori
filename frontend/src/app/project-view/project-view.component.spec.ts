import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProjectViewComponent } from './project-view.component';
import { ProjectService } from '../services/project.service';
import { AreaService } from '../services/area.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

describe('ProjectViewComponent', () => {
  let component: ProjectViewComponent;
  let fixture: ComponentFixture<ProjectViewComponent>;
  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let areaServiceSpy: jasmine.SpyObj<AreaService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockAreas = [
    { id: 1, name: 'Area A', leadName: 'Alice', leadEmail: 'alice@test.com' }
  ];

  const mockProjects = [
    {
      id: 1,
      projectName: 'Project 1',
      description: 'Test desc',
      developer: 'Dev 1',
      jira: 'http://jira/1',
      startDate: '2024-01-01',
      endDate: '2024-12-01',
      status: { statusName: 'In Progress', id: 1, percentage: 70 },
      area: mockAreas[0]
    }
  ];

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', ['getAllProjects', 'deleteProject']);
    areaServiceSpy = jasmine.createSpyObj('AreaService', ['getAreas']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ProjectViewComponent, MatSnackBarModule, MatDialogModule, RouterTestingModule],
      providers: [
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: AreaService, useValue: areaServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectViewComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should decode role from valid token', () => {
    localStorage.setItem('token', createMockJWT({ role: 'EDITOR' }));
    component.ngOnInit();
    expect(component.userRole).toBe('EDITOR');
  });

  it('should fall back to VIEWER on invalid token', () => {
    localStorage.setItem('token', 'invalid.token.payload');
    component.ngOnInit();
    expect(component.userRole).toBe('VIEWER');
  });

  it('should load projects and group them by area', fakeAsync(() => {
    areaServiceSpy.getAreas.and.returnValue(of(mockAreas));
    projectServiceSpy.getAllProjects.and.returnValue(of(mockProjects));

    component.ngOnInit();
    tick();

    expect(component.areaNames).toEqual(['Area A']);
    expect(component.groupedProjects['Area A'].data.length).toBe(1);
  }));

  it('should show error dialog if project fetch fails', fakeAsync(() => {
    areaServiceSpy.getAreas.and.returnValue(of(mockAreas));
    projectServiceSpy.getAllProjects.and.returnValue(throwError(() => new Error()));

    component.loadProjects();
    tick();

    expect(component.error).toBeTrue();
    expect(dialogSpy.open).toHaveBeenCalled();
  }));

  it('should navigate to edit on editProject()', () => {
    component.editProject({ id: 99 });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/edit-project', 99]);
  });

  it('should open dialog and delete project on confirmation', fakeAsync(() => {
    component.dataSource = [...mockProjects];
    projectServiceSpy.getAllProjects.and.returnValue(of([]));
    projectServiceSpy.deleteProject.and.returnValue(of({}));
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) } as any);

    component.deleteProject(mockProjects[0]);
    tick();

    expect(projectServiceSpy.deleteProject).toHaveBeenCalledWith(1);
    expect(component.dataSource.length).toBeGreaterThanOrEqual(0);
  }));

  function createMockJWT(payload: object): string {
    const base64 = btoa(JSON.stringify(payload));
    return `header.${base64}.signature`;
  }
});
