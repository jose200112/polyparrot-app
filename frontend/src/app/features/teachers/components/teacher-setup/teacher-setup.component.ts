import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { TeacherService } from '../../services/teacher.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-teacher-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './teacher-setup.component.html',
  styleUrl: './teacher-setup.component.css'
})
export class TeacherSetupComponent implements OnInit, OnDestroy {

  teacher = {
    bio: '',
    pricePerHour: null as number | null,
    teachingLanguages: [] as any[],
    spokenLanguages: [] as any[]
  };

  languages: any[] = [];

  showTeaching = false;
  showSpoken = false;

  frontErrors: Record<string, string> = {};
  backErrors: Record<string, string> = {};

  formSubmitted = false;

  constructor(
    private teacherService: TeacherService,
    private router: Router
  ) {}

  ngOnInit(): void {
    document.body.classList.add('student-layout');
    this.loadLanguages();
  }

  ngOnDestroy(): void {
    document.body.classList.remove('student-layout');
  }

  loadLanguages() {
    this.teacherService.getLanguages().subscribe({
      next: (res) => this.languages = res,
      error: () => this.backErrors['general'] = 'Error cargando idiomas'
    });
  }

  // ── VALIDACIÓN ──────────────────────────────────────────
  validateForm(): boolean {
    this.frontErrors = {};

if (this.teacher.bio && this.teacher.bio.length > 200) {
  this.frontErrors['bio'] = 'La bio no puede superar los 200 caracteres';
}

    if (this.teacher.pricePerHour === null || this.teacher.pricePerHour === undefined) {
      this.frontErrors['price'] = 'El precio por hora es obligatorio';
    } else if (this.teacher.pricePerHour < 1) {
      this.frontErrors['price'] = 'El precio mínimo es 1€';
    } else if (this.teacher.pricePerHour > 500) {
      this.frontErrors['price'] = 'El precio máximo es 500€';
    }

    if (this.teacher.teachingLanguages.length === 0) {
      this.frontErrors['teaching'] = 'Debes seleccionar al menos un idioma que enseñas';
    }

    if (this.teacher.spokenLanguages.length === 0) {
      this.frontErrors['spoken'] = 'Debes seleccionar al menos un idioma que hablas';
    }

    return Object.keys(this.frontErrors).length === 0;
  }

  onInputChange(field: string) {
    delete this.backErrors[field];
    delete this.backErrors['general'];
    this.validateForm();
  }

  submit() {
    this.formSubmitted = true;
    if (!this.validateForm()) return;

    this.backErrors = {};

    const payload = {
      bio: this.teacher.bio,
      pricePerHour: this.teacher.pricePerHour,
      teachingLanguageIds: this.teacher.teachingLanguages.map(l => l.id),
      spokenLanguageIds: this.teacher.spokenLanguages.map(l => l.id)
    };

    this.teacherService.createTeacher(payload).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        if (err.status === 400 && err.error && typeof err.error === 'object') {
          const map: Record<string, string> = {
            pricePerHour: 'price',
            teachingLanguageIds: 'teaching',
            spokenLanguageIds: 'spoken',
            bio: 'bio'
          };
          Object.entries(err.error).forEach(([field, msg]) => {
            const key = map[field] ?? field;
            this.backErrors[key] = msg as string;
          });
        } else {
          this.backErrors['general'] = 'Error creando perfil de profesor';
        }
      }
    });
  }

  // ── TEACHING ────────────────────────────────────────────
  get availableTeachingLanguages() {
    return this.languages.filter(lang =>
      !this.teacher.teachingLanguages.find(l => l.id === lang.id)
    );
  }

  toggleTeaching() {
    this.showTeaching = !this.showTeaching;
    this.showSpoken = false;
  }

  selectTeaching(lang: any) {
    this.teacher.teachingLanguages.push(lang);
    delete this.frontErrors['teaching'];
    delete this.backErrors['teaching'];
    this.showTeaching = false;
  }

  removeTeaching(lang: any) {
    this.teacher.teachingLanguages =
      this.teacher.teachingLanguages.filter(l => l.id !== lang.id);
    if (this.formSubmitted) this.validateForm();
  }

  // ── SPOKEN ──────────────────────────────────────────────
  get availableSpokenLanguages() {
    return this.languages.filter(lang =>
      !this.teacher.spokenLanguages.find(l => l.id === lang.id)
    );
  }

  toggleSpoken() {
    this.showSpoken = !this.showSpoken;
    this.showTeaching = false;
  }

  selectSpoken(lang: any) {
    this.teacher.spokenLanguages.push(lang);
    delete this.frontErrors['spoken'];
    delete this.backErrors['spoken'];
    this.showSpoken = false;
  }

  removeSpoken(lang: any) {
    this.teacher.spokenLanguages =
      this.teacher.spokenLanguages.filter(l => l.id !== lang.id);
    if (this.formSubmitted) this.validateForm();
  }

  // ── CIERRE AL HACER CLICK FUERA ─────────────────────────
  @HostListener('document:click')
  clickOutside() {
    this.showTeaching = false;
    this.showSpoken = false;
  }
}