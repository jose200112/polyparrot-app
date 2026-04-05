import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

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

constructor(
  private http: HttpClient,
  private authService: AuthService,
  private router: Router
) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.loadNotifications();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  loadNotifications() {
    const userId = this.authService.getUserId();
    this.http.get<any[]>(`http://localhost:8083/notifications/${userId}`).subscribe({
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
    this.http.patch(`http://localhost:8083/notifications/${userId}/read-all`, {}).subscribe();
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
  if (notification.type === 'BOOKING_CREATED') {
    this.router.navigate(['/teacher/home']);
  } else if (notification.type === 'BOOKING_CONFIRMED') {
    this.router.navigate(['/student/home']);
  }
}
}