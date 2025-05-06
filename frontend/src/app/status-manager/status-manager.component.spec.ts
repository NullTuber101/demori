import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { StatusManagerComponent } from './status-manager.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { StatusService } from '../services/status.service';

describe('StatusManagerComponent', () => {
  let component: StatusManagerComponent;
  let fixture: ComponentFixture<StatusManagerComponent>;
  let statusServiceSpy: jasmine.SpyObj<StatusService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    statusServiceSpy = jasmine.createSpyObj('StatusService', ['getStatuses', 'addStatus', 'updateStatus', 'deleteStatus']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MatSnackBarModule],
      declarations: [StatusManagerComponent],
      providers: [
        { provide: StatusService, useValue: statusServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(StatusManagerComponent);
    component = fixture.componentInstance;
    component.isLoggedIn = true;
    component.userRole = 'SUPER_USER';
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load and sort statuses', () => {
    const mockStatuses = [
      { id: 1, statusName: 'Done', percentage: 100 },
      { id: 2, statusName: 'In Progress', percentage: 50 }
    ];
    statusServiceSpy.getStatuses.and.returnValue(of(mockStatuses));
    component.loadStatuses();
    expect(component.statuses.length).toBe(2);
    expect(statusServiceSpy.getStatuses).toHaveBeenCalled();
  });

  it('should add a new status', () => {
    const newStatus = { id: 3, statusName: 'New', percentage: 10 };
    statusServiceSpy.addStatus.and.returnValue(of(newStatus));

    component.statusForm.setValue({ statusName: 'New', percentage: 10 });
    component.addStatus();

    expect(statusServiceSpy.addStatus).toHaveBeenCalled();
    expect(component.statuses).toContain(newStatus);
  });

  it('should enable edit mode for a status', () => {
    component.statuses = [{ id: 1, statusName: 'Test', percentage: 25 }];
    component.enableEdit(0);
    expect(component.editIndex).toBe(0);
    expect(component.editForm.value.statusName).toBe('Test');
  });

  it('should update a status', () => {
    const updatedStatus = { id: 1, statusName: 'Updated', percentage: 70 };
    component.statuses = [{ id: 1, statusName: 'Old', percentage: 50 }];
    component.editIndex = 0;
    component.editForm.setValue(updatedStatus);
    statusServiceSpy.updateStatus.and.returnValue(of(updatedStatus));

    component.saveEdit(0);
    expect(statusServiceSpy.updateStatus).toHaveBeenCalledWith(updatedStatus);
    expect(component.statuses[0].statusName).toBe('Updated');
    expect(component.editIndex).toBeNull();
  });

  it('should handle cancel edit', () => {
    component.editIndex = 0;
    component.cancelEdit();
    expect(component.editIndex).toBeNull();
  });

  it('should confirm and delete status', fakeAsync(() => {
    component.statuses = [{ id: 1, statusName: 'To Delete', percentage: 20 }];
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) } as any);
    statusServiceSpy.deleteStatus.and.returnValue(of(void 0));

    component.confirmDelete(0);
    tick();

    expect(statusServiceSpy.deleteStatus).toHaveBeenCalledWith(1);
    expect(component.statuses.length).toBe(0);
  }));
});