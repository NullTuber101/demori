import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminService } from './admin.service';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminService]
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // ensure no pending requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch pending requests', () => {
    const dummyData = [{ id: 1, name: 'Request 1' }];
    service.getPendingRequests().subscribe(data => {
      expect(data).toEqual(dummyData);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/requests/pending');
    expect(req.request.method).toBe('GET');
    req.flush(dummyData);
  });

  it('should approve a request', () => {
    service.approveRequest(1, 'ADMIN').subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('http://localhost:8080/api/requests/1/approve?roleName=ADMIN');
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should reject a request', () => {
    service.rejectRequest(2, 'Not qualified').subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('http://localhost:8080/api/requests/2/reject?reason=Not%20qualified');
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should fetch approved users', () => {
    const users = [{ id: 1, name: 'John' }];
    service.getApprovedUsers().subscribe(data => {
      expect(data).toEqual(users);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/users');
    expect(req.request.method).toBe('GET');
    req.flush(users);
  });

  it('should update user role', () => {
    const userId = 3;
    const role = 'MODERATOR';

    service.updateUserRole(userId, role).subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/users/3/role`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ roleName: role });
    req.flush({});
  });
});
