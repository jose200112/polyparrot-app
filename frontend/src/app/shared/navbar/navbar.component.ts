import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  menuOpen = false;
  unreadCount = 0;
  private pollSub?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    if (this.isLoggedIn()) {
      this.loadUnreadCount();
      // Polling cada 30 segundos
      this.pollSub = interval(30000).pipe(
        switchMap(() => this.fetchUnreadCount())
      ).subscribe(count => this.unreadCount = count);
    }
  }

  ngOnDestroy() {
    this.pollSub?.unsubscribe();
  }

  fetchUnreadCount() {
    const userId = this.authService.getUserId();
    return this.http.get<number>(`http://localhost:8083/notifications/${userId}/unread-count`);
  }

  loadUnreadCount() {
    this.fetchUnreadCount().subscribe({
      next: (count) => this.unreadCount = count,
      error: () => {}
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.authService.logout();
    this.menuOpen = false;
    this.unreadCount = 0;
    this.router.navigateByUrl('/');
  }

  isTeacher(): boolean {
    return this.authService.getRole() === 'TEACHER';
  }
}