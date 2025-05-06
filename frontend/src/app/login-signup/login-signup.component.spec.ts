import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginSignupComponent } from './login-signup.component';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';

describe('LoginSignupComponent', () => {
  let component: LoginSignupComponent;
  let fixture: ComponentFixture<LoginSignupComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'signup']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [LoginSignupComponent, MatSnackBarModule, MatDialogModule, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginSignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle between login and signup', () => {
    component.isLogin = true;
    component.toggleForm();
    expect(component.isLogin).toBeFalse();

    component.toggleForm();
    expect(component.isLogin).toBeTrue();
  });

  it('should show error on invalid login', () => {
    component.loginForm.setValue({ brid: '', password: '' });
    component.onLogin();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
    expect(dialogSpy.open).toHaveBeenCalled(); // error dialog shown
  });

  it('should perform successful login', fakeAsync(() => {
    const mockRes = { token: '123', role: 'ADMIN', name: 'Test' };
    component.loginForm.setValue({ brid: 'test01', password: 'pass123' });
    authServiceSpy.login.and.returnValue(of(mockRes));
    spyOn(location, 'reload');

    component.onLogin();
    tick();

    expect(localStorage.getItem('token')).toBe('123');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    expect(location.reload).toHaveBeenCalled();
  }));

  it('should show alert on login error', fakeAsync(() => {
    component.loginForm.setValue({ brid: 'wrong', password: 'wrong' });
    authServiceSpy.login.and.returnValue(throwError(() => ({ error: { message: 'Invalid user' } })));

    component.onLogin();
    tick();

    expect(dialogSpy.open).toHaveBeenCalled();
  }));

  it('should show validation errors on invalid signup', () => {
    component.isLogin = false;
    component.toggleForm();
    component.signupForm.setValue({
      name: '',
      brid: '',
      email: 'invalid',
      password: 'abc',
      confirmPassword: 'xyz'
    });

    component.onSignup();
    expect(authServiceSpy.signup).not.toHaveBeenCalled();
    expect(dialogSpy.open).toHaveBeenCalled(); // shows mismatch or email error
  });

  it('should perform successful signup', fakeAsync(() => {
    component.isLogin = false;
    component.toggleForm();
    component.signupForm.setValue({
      name: 'Test',
      brid: 'T01',
      email: 'test@email.com',
      password: 'abc123',
      confirmPassword: 'abc123'
    });

    authServiceSpy.signup.and.returnValue(of({ message: 'Signup success' }));
    component.onSignup();
    tick();

    expect(authServiceSpy.signup).toHaveBeenCalled();
  }));

  it('should show error on signup failure', fakeAsync(() => {
    component.isLogin = false;
    component.toggleForm();
    component.signupForm.setValue({
      name: 'Test',
      brid: 'T01',
      email: 'test@email.com',
      password: 'abc123',
      confirmPassword: 'abc123'
    });

    authServiceSpy.signup.and.returnValue(throwError(() => ({ error: 'Signup error' })));
    component.onSignup();
    tick();

    expect(dialogSpy.open).toHaveBeenCalled();
  }));
});
