import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { interval, Subscription } from 'rxjs';
import { switchMap, filter } from 'rxjs/operators';
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
  private subs = new Subscription();
  private connected = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private notificationService: NotificationService 
  ) {}

ngOnInit() {
  this.subs.add(
    this.notificationService.newNotification$.subscribe(() => {
      this.unreadCount++;
    })
  );

  this.subs.add(
    this.notificationService.readAll$.subscribe(() => {
      this.unreadCount = 0;
    })
  );

  // The navbar lives outside <router-outlet>, so it is created only once.
  // Re-check the session after every navigation (e.g. right after login).
  this.subs.add(
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.syncSession())
  );

  this.syncSession();
}

  private syncSession() {
    if (this.isLoggedIn() && !this.connected) {
      this.connected = true;
      this.loadUnreadCount();
      this.notificationService.connect();
    } else if (!this.isLoggedIn() && this.connected) {
      this.connected = false;
      this.notificationService.disconnect();
      this.unreadCount = 0;
    }
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
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
    this.syncSession();
    this.router.navigateByUrl('/');
  }

  isTeacher(): boolean {
    return this.authService.getRole() === 'TEACHER';
  }

}
