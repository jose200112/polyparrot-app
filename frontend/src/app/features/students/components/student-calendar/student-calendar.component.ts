import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-student-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-calendar.component.html',
  styleUrl: './student-calendar.component.css'
})
export class StudentCalendarComponent implements OnInit, OnDestroy {

  weekStart: Date = this.getMonday(new Date());
  weekDays: string[] = [];
  hours: number[] = Array.from({ length: 14 }, (_, i) => i + 8);
  bookings: any[] = [];
  error = '';

  // ── POPUP ────────────────────────────────────────────
  showPopup = false;
  selectedBooking: any = null;
  cancelError = '';
  cancelSuccess = false;

  readonly DAYS = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.buildWeek();
    this.loadBookings();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  buildWeek() {
    this.weekDays = Array.from({ length: 7 }, (_, i) => {
      const d = new Date(this.weekStart);
      d.setDate(d.getDate() + i);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${day}`;
    });
  }

  prevWeek() {
    if (this.isCurrentWeek()) return;
    this.weekStart.setDate(this.weekStart.getDate() - 7);
    this.weekStart = new Date(this.weekStart);
    this.buildWeek();
  }

  nextWeek() {
    this.weekStart.setDate(this.weekStart.getDate() + 7);
    this.weekStart = new Date(this.weekStart);
    this.buildWeek();
  }

  isCurrentWeek(): boolean {
    const now = this.getMonday(new Date());
    return this.weekStart.toDateString() === now.toDateString();
  }

  getMonday(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = day === 0 ? -6 : 1 - day;
    d.setDate(d.getDate() + diff);
    d.setHours(0, 0, 0, 0);
    return d;
  }

  weekLabel(): string {
    const end = new Date(this.weekStart);
    end.setDate(end.getDate() + 6);
    return `${this.weekStart.getDate()}/${this.weekStart.getMonth() + 1} - ${end.getDate()}/${end.getMonth() + 1}`;
  }

  getDayNumber(dateStr: string): number {
    return Number(dateStr.split('-')[2]);
  }

  isPast(dateStr: string, hour: number): boolean {
    const [y, m, d] = dateStr.split('-').map(Number);
    return new Date(y, m - 1, d, hour, 0, 0) < new Date();
  }

  loadBookings() {
    this.http.get<any[]>('http://localhost:8082/bookings/me').subscribe({
      next: (res) => this.bookings = res,
      error: () => this.error = 'Error cargando tus clases'
    });
  }

  getBooking(dateStr: string, hour: number): any | null {
    return this.bookings.find(b => {
      const [datePart, timePart] = (b.startTime as string).split('T');
      return datePart === dateStr && Number(timePart.split(':')[0]) === hour;
    }) ?? null;
  }

getCellClass(dateStr: string, hour: number): string {
  if (this.isPast(dateStr, hour)) return 'past';
  const booking = this.getBooking(dateStr, hour);
  if (!booking) return '';
  if (booking.status === 'CONFIRMED') return 'booked-confirmed';
  if (booking.status === 'PENDING') return 'booked-pending';
  if (booking.status === 'CANCELLED') return 'booked-cancelled';
  return '';
}

  openPopup(dateStr: string, hour: number) {
    const booking = this.getBooking(dateStr, hour);
    if (!booking) return;
    this.selectedBooking = booking;
    this.cancelError = '';
    this.cancelSuccess = false;
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
    this.selectedBooking = null;
    this.cancelError = '';
    this.cancelSuccess = false;
  }

  getTeacherName(booking: any): string {
    return [booking.teacherName, booking.teacherFirstSurname, booking.teacherSecondSurname]
      .filter(Boolean).join(' ');
  }

  formatHour(dateStr: string): string {
    const hour = Number((dateStr as string).split('T')[1].split(':')[0]);
    return `${String(hour).padStart(2, '0')}:00 - ${String(hour + 1).padStart(2, '0')}:00`;
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('es-ES', {
      weekday: 'long', day: 'numeric', month: 'long'
    });
  }

  cancelBooking() {
    this.http.patch(`http://localhost:8082/bookings/${this.selectedBooking.id}/cancel`, {}).subscribe({
      next: () => {
        this.cancelSuccess = true;
        this.bookings = this.bookings.map(b =>
          b.id === this.selectedBooking.id ? { ...b, status: 'CANCELLED' } : b
        );
        setTimeout(() => this.closePopup(), 1500);
      },
      error: () => this.cancelError = 'Error al cancelar la reserva'
    });
  }

  canCancel(booking: any): boolean {
  if (!booking || booking.status !== 'CONFIRMED') return false;
  const startTime = new Date(booking.startTime);
  const now = new Date();
  const hoursUntilClass = (startTime.getTime() - now.getTime()) / (1000 * 60 * 60);
  return hoursUntilClass >= 24;
}

getCancelTooltip(booking: any): string {
  if (!booking || booking.status !== 'CONFIRMED') return '';
  const startTime = new Date(booking.startTime);
  const now = new Date();
  if (startTime < now) return 'Esta clase ya ha pasado';
  const hoursUntilClass = (startTime.getTime() - now.getTime()) / (1000 * 60 * 60);
  if (hoursUntilClass < 24) return 'Solo puedes cancelar con al menos 24h de antelación';
  return '';
}
}