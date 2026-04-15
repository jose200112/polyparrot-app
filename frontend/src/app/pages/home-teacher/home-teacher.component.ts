import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-home-teacher',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home-teacher.component.html',
  styleUrl: './home-teacher.component.css'
})
export class HomeTeacherComponent implements OnInit, OnDestroy {

  confirmedBookings: any[] = [];
  pendingBookings: any[] = [];
  slots: any[] = [];
  loading = true;
  error = '';
  teacherName = '';
  confirmError = '';
  confirmSuccess = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.loadTeacherName();
    this.loadBookings();
    this.loadSlots();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  loadTeacherName() {
    this.authService.getMe().subscribe({
      next: (res) => this.teacherName = res.name,
      error: () => {}
    });
  }

  loadBookings() {
    this.http.get<any[]>(`${environment.teacherServiceUrl}/teachers/me/bookings/upcoming`).subscribe({
      next: (res) => {
        const withinWeek = res
          .filter(b => this.isWithinDays(b.startTime, 7))
          .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());

        this.confirmedBookings = withinWeek.filter(b => b.status === 'CONFIRMED');
        this.pendingBookings = withinWeek.filter(b => b.status === 'PENDING');
        this.loading = false;
      },
      error: () => {
        this.error = 'Error cargando tus clases';
        this.loading = false;
      }
    });
  }

  loadSlots() {
    const teacherId = this.authService.getUserId();
    this.http.get<any[]>(`${environment.teacherServiceUrl}/availability/${teacherId}`).subscribe({
      next: (res) => this.slots = res,
      error: () => {}
    });
  }

  confirmBooking(bookingId: number) {
    this.http.patch(`${environment.bookingServiceUrl}/bookings/${bookingId}/confirm`, {}).subscribe({
      next: () => {
        const booking = this.pendingBookings.find(b => b.bookingId === bookingId);
        if (booking) {
          booking.status = 'CONFIRMED';
          this.pendingBookings = this.pendingBookings.filter(b => b.bookingId !== bookingId);
          this.confirmedBookings.push(booking);
          this.confirmedBookings.sort((a, b) =>
            new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
          );
        }
        this.confirmSuccess = '✅ Reserva confirmada';
        setTimeout(() => this.confirmSuccess = '', 3000);
      },
      error: () => this.confirmError = 'Error al confirmar la reserva'
    });
  }

  // ── HELPERS ──────────────────────────────────────────
  isWithinDays(dateStr: string, days: number): boolean {
    const d = new Date(dateStr);
    const limit = new Date();
    limit.setDate(limit.getDate() + days);
    return d <= limit;
  }

  hasUpcomingSlots(): boolean {
    const limit = new Date();
    limit.setDate(limit.getDate() + 7);
    return this.slots.some(s => new Date(s.startTime) > new Date() && new Date(s.startTime) <= limit);
  }

  get classesThisWeek(): number {
    return this.confirmedBookings.length;
  }

  get uniqueStudents(): number {
    return new Set(this.confirmedBookings.map(b => b.studentName + b.studentFirstSurname)).size;
  }

  get hoursThisWeek(): number {
    return this.confirmedBookings.length;
  }

  getStudentName(booking: any): string {
    return [booking.studentName, booking.studentFirstSurname, booking.studentSecondSurname]
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