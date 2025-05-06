import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { VelocitySectionComponent } from './velocity-section/velocity-section.component';
import { LoginSignupComponent } from './login-signup/login-signup.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { MatIcon } from '@angular/material/icon';
import { AuthBarComponent } from './auth-bar/auth-bar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent,AuthBarComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
   
}
