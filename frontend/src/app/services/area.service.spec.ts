import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AreaService, Area } from './area.service';
import { AuthService } from './auth.service';

describe('AreaService', () => {
  let service: AreaService;
  let httpMock: HttpTestingController;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['getToken']);
    mockAuthService.getToken.and.returnValue('mock-token');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AreaService,
        { provide: AuthService, useValue: mockAuthService }
      ]
    });

    service = TestBed.inject(AreaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  const mockArea: Area = {
    id: 1,
    name: 'Area 1',
    leadName: 'John Doe',
    leadEmail: 'john@example.com'
  };

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch areas with auth header', () => {
    service.getAreas().subscribe(areas => {
      expect(areas.length).toBe(1);
      expect(areas[0].name).toBe('Area 1');
    });

    const req = httpMock.expectOne('http://localhost:8080/api/areas');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush([mockArea]);
  });

  it('should add an area', () => {
    service.addArea(mockArea).subscribe(area => {
      expect(area).toEqual(mockArea);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/areas');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockArea);
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(mockArea);
  });

  it('should update an area', () => {
    const updatedArea = { ...mockArea, name: 'Updated Area' };

    service.updateArea(updatedArea).subscribe(area => {
      expect(area.name).toBe('Updated Area');
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/areas/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.name).toBe('Updated Area');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(updatedArea);
  });

  it('should delete an area', () => {
    service.deleteArea(1).subscribe(response => {
      expect(response).toBeNull(); 
    });
  
    const req = httpMock.expectOne(`http://localhost:8080/api/areas/1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(null); 
  });
});
