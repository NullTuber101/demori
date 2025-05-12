import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockToken = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' })) + '.' +
                    btoa(JSON.stringify({ role: 'SUPER_USER', sub: 'BR1234' })) + '.' +
                    'signature';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    // Mock localStorage
    spyOn(localStorage, 'setItem').and.callFake(() => {});
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      return key === 'token' ? mockToken : null;
    });
    spyOn(localStorage, 'removeItem').and.callFake(() => {});
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send login request', () => {
    const credentials = { brid: 'BR1234', password: 'pass' };

    service.login(credentials).subscribe(res => {
      expect(res).toEqual({ token: 'dummy-token' });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);
    req.flush({ token: 'dummy-token' });
  });

  it('should send signup request', () => {
    const reqData = {
      name: 'User',
      brid: 'BR1234',
      email: 'user@example.com',
      password: 'pass'
    };

    service.signup(reqData).subscribe(res => {
      expect(res).toEqual({ message: 'Signup success' });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/requests/signup');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(reqData);
    req.flush({ message: 'Signup success' });
  });

  it('should save token to localStorage', () => {
    service.setToken('abc123');
    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'abc123');
  });

  it('should get token from localStorage', () => {
    const token = service.getToken();
    expect(localStorage.getItem).toHaveBeenCalledWith('token');
    expect(token).toBe(mockToken);
  });

  it('should decode token and get role', () => {
    const role = service.getRole();
    expect(role).toBe('SUPER_USER');
  });

  it('should decode token and get BRID', () => {
    const brid = service.getBrid();
    expect(brid).toBe('BR1234');
  });

  it('should return true if logged in', () => {
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should logout and clear token and redirect to login', () => {
    spyOn(service as any, 'redirectToLogin'); // Use type cast if needed

    service.logout();

    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    expect((service as any).redirectToLogin).toHaveBeenCalled();
  });
});
