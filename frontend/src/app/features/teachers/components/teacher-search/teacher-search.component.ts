import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TeacherSearchService } from '../../services/teacher-search.service';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-teacher-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './teacher-search.component.html',
  styleUrl: './teacher-search.component.css'
})
export class TeacherSearchComponent implements OnInit {

  filters = {
    minPrice: null as number | null,
    maxPrice: null as number | null,
    teachingLanguage: '',
    spokenLanguage: '',
    date: '',
    hour: null as number | null,
    sortOrder: 'asc'
  };

  teachers: any[] = [];
  loading = false;
  error = '';
  searched = false;
  hours: number[] = Array.from({ length: 14 }, (_, i) => i + 8);

  // ── POPUP ────────────────────────────────────────────
  showPopup = false;
  selectedTeacher: any = null;
  bookingError = '';
  bookingSuccess = false;

  // Calendario
  step: 1 | 2 = 1;
  availableSlots: any[] = [];
  loadingSlots = false;

  currentMonth: Date = new Date();
  calendarDays: (Date | null)[] = [];
  availableDates: Set<string> = new Set();
  selectedDate: string = '';

  // Horas
  slotsForSelectedDay: any[] = [];
  selectedSlot: any = null;

  constructor(
    private teacherSearchService: TeacherSearchService,
    private authService: AuthService,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

ngOnInit() {
  this.route.queryParams.pipe(take(1)).subscribe(params => {
    if (params['teachingLanguage']) {
      this.filters.teachingLanguage = params['teachingLanguage'];
    }
    this.search();
  });
  document.body.classList.add('student-layout');
}


  ngOnDestroy(): void {
    document.body.classList.remove('student-layout');
  }

  buildStartTime(): string | null {
    if (!this.filters.date || this.filters.hour == null) return null;
    const hour = String(this.filters.hour).padStart(2, '0');
    return `${this.filters.date}T${hour}:00:00`;
  }

  search() {
    this.loading = true;
    this.error = '';
    this.searched = true;
    const payload = { ...this.filters, startTime: this.buildStartTime() };
    this.teacherSearchService.searchTeachers(payload).subscribe({
      next: (res) => { this.teachers = res; this.loading = false; },
      error: () => { this.error = 'Error buscando profesores'; this.loading = false; }
    });
  }

  clearFilters() {
    this.filters = { minPrice: null, maxPrice: null, teachingLanguage: '', spokenLanguage: '', date: '', hour: null, sortOrder: 'asc' };
    this.search();
  }

  today(): string {
    return new Date().toISOString().split('T')[0];
  }

  getFullName(teacher: any): string {
    return [teacher.name, teacher.firstSurname, teacher.secondSurname].filter(Boolean).join(' ');
  }

  getLanguageNames(languages: any[]): string {
    return languages?.map(l => l.name).join(', ') || '';
  }

  // ── POPUP ────────────────────────────────────────────
openBooking(teacher: any) {
  if (!this.authService.isLoggedIn()) {
    this.router.navigate(['/login']);
    return;
  }
  this.selectedTeacher = teacher;
  this.bookingError = '';
  this.bookingSuccess = false;
  this.step = 1;
  this.selectedDate = '';
  this.selectedSlot = null;
  this.currentMonth = new Date();
  this.showPopup = true;
  this.loadAvailableSlots(teacher.id);
}

  closePopup() {
    this.showPopup = false;
    this.selectedTeacher = null;
    this.bookingError = '';
    this.bookingSuccess = false;
    this.availableSlots = [];
    this.selectedSlot = null;
  }

  loadAvailableSlots(teacherId: number) {
    this.loadingSlots = true;
    this.http.get<any[]>(`http://localhost:8082/bookings/available/${teacherId}`).subscribe({
      next: (slots) => {
        this.availableSlots = slots;
        this.buildAvailableDates();
        this.buildCalendar();
        this.loadingSlots = false;
      },
      error: () => {
        this.bookingError = 'Error cargando disponibilidad';
        this.loadingSlots = false;
      }
    });
  }

  buildAvailableDates() {
    this.availableDates = new Set(
      this.availableSlots.map(slot => slot.startTime.split('T')[0])
    );
  }

  // ── CALENDARIO ───────────────────────────────────────
  buildCalendar() {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const offset = firstDay === 0 ? 6 : firstDay - 1; // lunes primero

    this.calendarDays = [];
    for (let i = 0; i < offset; i++) this.calendarDays.push(null);
    for (let d = 1; d <= daysInMonth; d++) this.calendarDays.push(new Date(year, month, d));
  }

  prevMonth() {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() - 1, 1);
    this.buildCalendar();
  }

  nextMonth() {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() + 1, 1);
    this.buildCalendar();
  }

  monthLabel(): string {
    return this.currentMonth.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' });
  }

  dateStr(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  isAvailable(date: Date): boolean {
    return this.availableDates.has(this.dateStr(date));
  }

  isPastDate(date: Date): boolean {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  }

  selectDay(date: Date) {
    if (!this.isAvailable(date)) return;
    this.selectedDate = this.dateStr(date);
    this.slotsForSelectedDay = this.availableSlots.filter(
      slot => slot.startTime.split('T')[0] === this.selectedDate
    ).sort((a, b) => a.startTime.localeCompare(b.startTime));
    this.selectedSlot = null;
    this.step = 2;
  }

  formatSlotHour(startTime: string): string {
    const hour = Number(startTime.split('T')[1].split(':')[0]);
    return `${String(hour).padStart(2, '0')}:00 - ${String(hour + 1).padStart(2, '0')}:00`;
  }

  goBackToCalendar() {
    this.step = 1;
    this.selectedSlot = null;
    this.bookingError = '';
  }

  // ── CONFIRMAR RESERVA ────────────────────────────────
  confirmBooking() {
    if (!this.selectedSlot) {
      this.bookingError = 'Selecciona un horario';
      return;
    }

    const startTime = this.selectedSlot.startTime;
    const endTime = this.selectedSlot.startTime.replace(/T(\d{2})/, (_: string, h: string) =>
      `T${String(Number(h) + 1).padStart(2, '0')}`
    );

    this.http.post('http://localhost:8082/bookings', {
      teacherId: this.selectedTeacher.id,
      startTime,
      endTime
    }).subscribe({
      next: () => {
        this.bookingSuccess = true;
        setTimeout(() => this.closePopup(), 1800);
      },
      error: (err) => {
        if (err.status === 409) this.bookingError = 'Ese horario ya está reservado';
        else if (err.status === 400) this.bookingError = 'Las reservas deben hacerse con al menos 24h de antelación';
        else this.bookingError = 'Error al realizar la reserva';
      }
    });
  }
}