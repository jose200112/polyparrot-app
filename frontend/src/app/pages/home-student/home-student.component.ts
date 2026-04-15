import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home-student',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home-student.component.html',
  styleUrls: ['./home-student.component.css']
})
export class HomeStudentComponent implements OnInit, OnDestroy {

  userName = '';
  bookings: any[] = [];
  loading = true;
  error = '';

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.loadUser();
    this.loadBookings();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  loadUser() {
    this.authService.getMe().subscribe({
      next: (user) => this.userName = user.name,
      error: () => {}
    });
  }

  loadBookings() {
    this.http.get<any[]>(`${environment.bookingServiceUrl}/bookings/me`).subscribe({
      next: (res) => {
        this.bookings = res
          .filter(b => b.status === 'CONFIRMED')
          .filter(b => this.isWithinDays(b.startTime, 7))
          .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());
        this.loading = false;
      },
      error: () => {
        this.error = 'Error cargando tus clases';
        this.loading = false;
      }
    });
  }

  // ── STATS ────────────────────────────────────────────
  get classesThisWeek(): number {
    return this.bookings.length;
  }

  get uniqueTeachers(): number {
    return new Set(this.bookings.map(b => b.teacherName + b.teacherFirstSurname)).size;
  }

  get hoursThisWeek(): number {
    return this.bookings.length;
  }

  // ── HELPERS ──────────────────────────────────────────
  isWithinDays(dateStr: string, days: number): boolean {
    const d = new Date(dateStr);
    const limit = new Date();
    limit.setDate(limit.getDate() + days);
    return d <= limit;
  }

  getTeacherName(booking: any): string {
    return [booking.teacherName, booking.teacherFirstSurname, booking.teacherSecondSurname]
      .filter(Boolean).join(' ');
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('es-ES', {
      weekday: 'long', day: 'numeric', month: 'long'
    });
  }

  formatHour(dateStr: string): string {
    const d = new Date(dateStr);
    const h = String(d.getHours()).padStart(2, '0');
    const end = String(d.getHours() + 1).padStart(2, '0');
    return `${h}:00 - ${end}:00`;
  }

  isToday(dateStr: string): boolean {
    const d = new Date(dateStr);
    const now = new Date();
    return d.getDate() === now.getDate() &&
           d.getMonth() === now.getMonth() &&
           d.getFullYear() === now.getFullYear();
  }

  isTomorrow(dateStr: string): boolean {
    const d = new Date(dateStr);
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return d.getDate() === tomorrow.getDate() &&
           d.getMonth() === tomorrow.getMonth() &&
           d.getFullYear() === tomorrow.getFullYear();
  }

  getDayLabel(dateStr: string): string {
    if (this.isToday(dateStr)) return 'Hoy';
    if (this.isTomorrow(dateStr)) return 'Mañana';
    return this.formatDate(dateStr);
  }
}