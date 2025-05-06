import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isSuperUser: boolean = false;

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    console.log(localStorage.getItem('token'));
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.isSuperUser = payload.role === 'SUPER_USER'; 
      } catch {
        this.isSuperUser = false;
      }
    }
  }
}
