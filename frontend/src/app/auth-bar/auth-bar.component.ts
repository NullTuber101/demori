import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-auth-bar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,       // ✅ Needed for routerLink to work
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './auth-bar.component.html',
  styleUrls: ['./auth-bar.component.css']
})
export class AuthBarComponent {
  constructor(private router: Router) {}

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getName(): string {
    return localStorage.getItem('name') || '';
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/']).then(() => {
      location.reload(); 
    });
  }
}
