import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { AdminService } from '../services/admin.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';

describe('AdminDashboardComponent', () => {
  let component: AdminDashboardComponent;
  let fixture: ComponentFixture<AdminDashboardComponent>;
  let adminServiceSpy: jasmine.SpyObj<AdminService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    adminServiceSpy = jasmine.createSpyObj('AdminService', [
      'getPendingRequests',
      'getApprovedUsers',
      'approveRequest',
      'rejectRequest',
      'updateUserRole'
    ]);

    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [MatSnackBarModule, MatDialogModule],
      providers: [
        { provide: AdminService, useValue: adminServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      declarations: [AdminDashboardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch requests and users on init', () => {
    adminServiceSpy.getPendingRequests.and.returnValue(of([]));
    adminServiceSpy.getApprovedUsers.and.returnValue(of([]));

    component.ngOnInit();

    expect(adminServiceSpy.getPendingRequests).toHaveBeenCalled();
    expect(adminServiceSpy.getApprovedUsers).toHaveBeenCalled();
  });

  it('should approve request after confirmation', fakeAsync(() => {
    const mockDialogRef = {
      afterClosed: () => of(true)
    } as any;
    dialogSpy.open.and.returnValue(mockDialogRef);
    adminServiceSpy.approveRequest.and.returnValue(of({ message: 'Approved' }));
    adminServiceSpy.getPendingRequests.and.returnValue(of([]));
    adminServiceSpy.getApprovedUsers.and.returnValue(of([]));

    component.approve(1, 'EDITOR');
    tick();

    expect(adminServiceSpy.approveRequest).toHaveBeenCalledWith(1, 'EDITOR');
  }));

  it('should reject request after confirmation', fakeAsync(() => {
    const mockDialogRef = {
      afterClosed: () => of(true)
    } as any;
    dialogSpy.open.and.returnValue(mockDialogRef);
    adminServiceSpy.rejectRequest.and.returnValue(of({}));

    component.reject(2);
    tick();

    expect(adminServiceSpy.rejectRequest).toHaveBeenCalledWith(2, 'Rejected by admin');
  }));

  it('should submit role update after confirmation', fakeAsync(() => {
    const user = {
      id: 5,
      name: 'Test User',
      role: { roleName: 'VIEWER' },
      pendingRole: 'EDITOR',
      editing: true
    };

    component.approvedUsers.set([user]);

    const mockDialogRef = {
      afterClosed: () => of(true)
    } as any;
    dialogSpy.open.and.returnValue(mockDialogRef);
    adminServiceSpy.updateUserRole.and.returnValue(of({}));

    component.submitRoleUpdate(0);
    tick();

    expect(adminServiceSpy.updateUserRole).toHaveBeenCalledWith(5, 'EDITOR');
  }));
});
