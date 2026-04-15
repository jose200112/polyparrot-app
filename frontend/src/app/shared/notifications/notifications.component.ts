import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../core/services/notification.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit, OnDestroy {

  notifications: any[] = [];
  loading = true;
  error = '';
private notifSub?: Subscription;

constructor(
  private http: HttpClient,
  private authService: AuthService,
  private router: Router,
  private notificationService: NotificationService
) {}

ngOnInit() {
  document.body.classList.add('student-layout');
  this.loadNotifications();

  this.notifSub = this.notificationService.newNotification$.subscribe((notif) => {
    this.notifications.unshift(notif);
  });
}

ngOnDestroy() {
  document.body.classList.remove('student-layout');
  this.notifSub?.unsubscribe();
}

loadNotifications() {
  const userId = this.authService.getUserId();
  this.http.get<any[]>(`${environment.notificationServiceUrl}/notifications/${userId}`).subscribe({
    next: (res) => {
      this.notifications = res;
      this.loading = false;
      this.markAllAsRead(userId!);
    },
    error: () => {
      this.error = 'Error cargando notificaciones';
      this.loading = false;
    }
  });
}

markAllAsRead(userId: string) {
  this.http.patch(`${environment.notificationServiceUrl}/notifications/${userId}/read-all`, {}).subscribe({
    next: () => this.notificationService.notifyReadAll() 
  });
}

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('es-ES', {
      day: 'numeric', month: 'long', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  getIcon(type: string): string {
    switch (type) {
      case 'BOOKING_CREATED': return '📅';
      case 'BOOKING_CONFIRMED': return '✅';
      case 'BOOKING_CANCELLED': return '❌';
      default: return '🔔';
    }
  }

navigate(notification: any) {
  if (this.authService.getRole() === 'TEACHER') {
    this.router.navigate(['/teacher/calendar']);
  } else {
    this.router.navigate(['/student/calendar']);
  }
}


}