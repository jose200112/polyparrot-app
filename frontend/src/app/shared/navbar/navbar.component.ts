import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { NotificationService } from '../../core/services/notification.service';
import { environment } from '../../../environments/environment';

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
  private notifSub?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private notificationService: NotificationService 
  ) {}

ngOnInit() {
  if (this.isLoggedIn()) {
    this.loadUnreadCount();
    this.notificationService.connect();

    this.notifSub = this.notificationService.newNotification$.subscribe(() => {
      this.unreadCount++;
    });

    // ← añadir
    this.notificationService.readAll$.subscribe(() => {
      this.unreadCount = 0;
    });
  }
}

  

  ngOnDestroy() {
    this.notifSub?.unsubscribe();
    this.notificationService.disconnect(); 
  }

  fetchUnreadCount() {
    const userId = this.authService.getUserId();
    return this.http.get<number>(`${environment.notificationServiceUrl}/notifications/${userId}/unread-count`);
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