import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AvailabilityService } from '../../services/availability.service';
import { AuthService } from '../../../../core/services/auth.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-availability',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './teacher-calendar.component.html',
  styleUrl: './teacher-calendar.component.css'
})
export class TeacherCalendarComponent implements OnInit, OnDestroy {

  weekStart: Date = this.getMonday(new Date());
  weekDays: string[] = [];
  hours: number[] = Array.from({ length: 14 }, (_, i) => i + 8);
  slots: any[] = [];
  bookings: any[] = [];
  error = '';

  // ── POPUP ────────────────────────────────────────────
  showPopup = false;
  selectedBooking: any = null;
  confirmSuccess = false;
  confirmError = '';

  readonly DAYS = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  constructor(
    private availabilityService: AvailabilityService,
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.buildWeek();
    this.loadSlots();
    this.loadBookings();
    document.body.classList.add('student-layout');
  }

  ngOnDestroy(): void {
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

  loadSlots() {
    const teacherId = Number(this.authService.getUserId());
    this.availabilityService.getSlots(teacherId).subscribe({
      next: (res) => this.slots = res,
      error: () => this.error = 'Error cargando disponibilidad'
    });
  }

  loadBookings() {
    this.http.get<any[]>(`${environment.teacherServiceUrl}/teachers/me/bookings/upcoming`).subscribe({
      next: (res) => this.bookings = res,
      error: () => {}
    });
  }

  getDayNumber(dateStr: string): number {
    return Number(dateStr.split('-')[2]);
  }

  isSlotActive(dateStr: string, hour: number): boolean {
    return this.slots.some(slot => {
      const [datePart, timePart] = (slot.startTime as string).split('T');
      return datePart === dateStr && Number(timePart.split(':')[0]) === hour;
    });
  }

  getSlotId(dateStr: string, hour: number): number | null {
    const slot = this.slots.find(slot => {
      const [datePart, timePart] = (slot.startTime as string).split('T');
      return datePart === dateStr && Number(timePart.split(':')[0]) === hour;
    });
    return slot ? slot.id : null;
  }

getBooking(dateStr: string, hour: number): any | null {
  const matches = this.bookings.filter(b => {
    const [datePart, timePart] = (b.startTime as string).split('T');
    return datePart === dateStr && Number(timePart.split(':')[0]) === hour;
  });

  if (matches.length === 0) return null;

  // Priorizar PENDING > CONFIRMED > CANCELLED más reciente
  const pending = matches.find(b => b.status === 'PENDING');
  if (pending) return pending;

  const confirmed = matches.find(b => b.status === 'CONFIRMED');
  if (confirmed) return confirmed;

  // Si solo hay canceladas, devolver la más reciente por id
  return matches.sort((a, b) => b.id - a.id)[0];
}

  getBookingStatus(dateStr: string, hour: number): string | null {
    const booking = this.getBooking(dateStr, hour);
    if (!booking) return null;
    return booking.status?.toLowerCase() ?? null;
  }

  isPast(dateStr: string, hour: number): boolean {
    const [y, m, d] = dateStr.split('-').map(Number);
    return new Date(y, m - 1, d, hour, 0, 0) < new Date();
  }

  toggleSlot(dateStr: string, hour: number) {
    if (this.isPast(dateStr, hour)) return;

    const booking = this.getBooking(dateStr, hour);
    if (booking) {
      this.openPopup(booking);
      return;
    }

    const slotId = this.getSlotId(dateStr, hour);
    if (slotId !== null) {
      this.availabilityService.deleteSlot(slotId).subscribe({
        next: () => this.slots = this.slots.filter(s => s.id !== slotId),
        error: () => this.error = 'No se puede eliminar: tiene una reserva'
      });
    } else {
      const hourStr = String(hour).padStart(2, '0');
      const iso = `${dateStr}T${hourStr}:00:00`;
      this.availabilityService.createSlot(iso).subscribe({
        next: (slot) => this.slots.push(slot),
        error: () => this.error = 'Error creando slot'
      });
    }
  }

  // ── POPUP ────────────────────────────────────────────
  openPopup(booking: any) {
    this.selectedBooking = booking;
    this.confirmSuccess = false;
    this.confirmError = '';
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
    this.selectedBooking = null;
    this.confirmSuccess = false;
    this.confirmError = '';
  }

  getStudentName(booking: any): string {
    return [booking.studentName, booking.studentFirstSurname, booking.studentSecondSurname]
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

  confirmBooking() {
    this.http.patch(`${environment.bookingServiceUrl}/bookings/${this.selectedBooking.bookingId}/confirm`, {})
      .subscribe({
        next: () => {
          this.bookings = this.bookings.map(b =>
            b.bookingId === this.selectedBooking.bookingId
              ? { ...b, status: 'CONFIRMED' }
              : b
          );
          this.selectedBooking = { ...this.selectedBooking, status: 'CONFIRMED' };
          this.confirmSuccess = true;
          setTimeout(() => this.closePopup(), 1500);
        },
        error: () => this.confirmError = 'Error al confirmar la reserva'
      });
  }

  cancelBooking() {
  this.http.patch(`${environment.bookingServiceUrl}/bookings/${this.selectedBooking.bookingId}/cancel-by-teacher`, {})
    .subscribe({
      next: () => {
        this.bookings = this.bookings.map(b =>
          b.bookingId === this.selectedBooking.bookingId
            ? { ...b, status: 'CANCELLED' }
            : b
        );
        this.selectedBooking = { ...this.selectedBooking, status: 'CANCELLED' };
        setTimeout(() => this.closePopup(), 1500);
      },
      error: (err) => {
        if (err.status === 400) this.confirmError = err.error;
        else this.confirmError = 'Error al cancelar la reserva';
      }
    });
}

deleteSlotFromPopup() {
  const slotId = this.getSlotId(
    this.selectedBooking.startTime.split('T')[0],
    Number(this.selectedBooking.startTime.split('T')[1].split(':')[0])
  );
  if (slotId === null) {
    this.closePopup();
    return;
  }
  this.availabilityService.deleteSlot(slotId).subscribe({
    next: () => {
      this.slots = this.slots.filter(s => s.id !== slotId);
      this.closePopup();
    },
    error: () => this.confirmError = 'Error al eliminar la disponibilidad'
  });
}
}