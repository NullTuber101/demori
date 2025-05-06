import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

import { AuthService } from '../services/auth.service';
import { AlertComponent } from '../alert/alert.component';

@Component({
  selector: 'app-login-signup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatSnackBarModule,
    MatDialogModule,
    MatIconModule
  ],
  templateUrl: './login-signup.component.html',
  styleUrls: ['./login-signup.component.css'],
  providers: [AuthService]
})
export class LoginSignupComponent {
  loginForm: FormGroup;
  signupForm: FormGroup;
  isLogin = true;

  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.loginForm = this.fb.group({
      brid: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      brid: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ mismatch: true });
      return { mismatch: true };
    }
    return null;
  }

  toggleForm() {
  this.isLogin = !this.isLogin;
  this.showPassword = false;
  this.showConfirmPassword = false;
}

  onLogin() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.openAlert('error', 'Validation Error', 'Please enter both BRID and password.');
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
        localStorage.setItem('name', res.name);
        this.snackBar.open('Login successful!', 'Close', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.router.navigate(['/']).then(() => location.reload());
      },
      error: (err) => {
        const msg = err?.error?.message || err?.error || 'Invalid BRID or password.';
        this.openAlert('error', 'Login Failed', msg);
      }
    });
  }

  onSignup() {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();

      const emailCtrl = this.signupForm.get('email');
      const password = this.signupForm.get('password')?.value;
      const confirmPassword = this.signupForm.get('confirmPassword')?.value;

      if (emailCtrl?.hasError('email')) {
        this.openAlert('error', 'Invalid Email', 'Please enter a valid email address.');
      } else if (password !== confirmPassword) {
        this.openAlert('error', 'Password Mismatch', 'Password and Confirm Password do not match.');
      } else {
        this.openAlert('error', 'Validation Error', 'All fields are required and must be valid.');
      }

      return;
    }

    const { confirmPassword, ...payload } = this.signupForm.value;

    this.authService.signup(payload).subscribe({
      next: () => {
        this.snackBar.open('Signup request submitted successfully.', 'Close', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.signupForm.reset();
      },
      error: (err) => {
        let msg = '';
        if (typeof err?.error === 'string') {
          msg = err.error;
        } else if (Array.isArray(err?.error?.message)) {
          msg = err.error.message[0].split(':')[1]?.trim() || 'Validation failed.';
        } else {
          msg = err?.error?.message || 'Signup failed. Please check your input.';
        }
        this.openAlert('error', 'Signup Failed', msg);
      }
    });
  }

  openAlert(type: 'success' | 'info' | 'error' | 'warning', title: string, message: string) {
    if (type === 'error') {
      this.dialog.open(AlertComponent, {
        data: { title, message }
      });
    } else {
      this.snackBar.open(message, 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'bottom',
        panelClass: ['snackbar-success']
      });
    }
  }
}
