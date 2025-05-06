import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthBarComponent } from './auth-bar.component';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

describe('AuthBarComponent', () => {
  let component: AuthBarComponent;
  let fixture: ComponentFixture<AuthBarComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [AuthBarComponent],
      providers: [
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should return true from isLoggedIn when token exists', () => {
    localStorage.setItem('token', 'dummy-token');
    expect(component.isLoggedIn()).toBeTrue();
  });

  it('should return false from isLoggedIn when no token', () => {
    localStorage.removeItem('token');
    expect(component.isLoggedIn()).toBeFalse();
  });

  it('should return the stored name from getName()', () => {
    localStorage.setItem('name', 'Test User');
    expect(component.getName()).toBe('Test User');
  });

  it('should clear localStorage and navigate on logout', () => {
    spyOn(location, 'reload');
    localStorage.setItem('token', 'abc');
    localStorage.setItem('name', 'User');
    component.logout();
    expect(localStorage.getItem('token')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    expect(location.reload).toHaveBeenCalled();
  });
});
