import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';
import { TeacherService } from '../../services/teacher.service';

@Component({
  selector: 'app-teacher-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './teacher-profile.component.html',
  styleUrl: './teacher-profile.component.css'
})
export class TeacherProfileComponent implements OnInit, OnDestroy {

  // ── DATOS PERSONALES ─────────────────────────────────
  userData = {
    name: '',
    firstSurname: '',
    secondSurname: ''
  };

  userErrors: Record<string, string> = {};
  userSuccess = false;
  userLoading = false;

  // ── DATOS PROFESOR ───────────────────────────────────
  teacherData = {
    bio: '',
    pricePerHour: null as number | null,
    teachingLanguages: [] as any[],
    spokenLanguages: [] as any[]
  };

  languages: any[] = [];
  showTeaching = false;
  showSpoken = false;
  teacherErrors: Record<string, string> = {};
  teacherSuccess = false;
  teacherLoading = false;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private teacherService: TeacherService
  ) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.loadUserData();
    this.loadTeacherData();
    this.loadLanguages();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  // ── CARGA DE DATOS ───────────────────────────────────
loadUserData() {
  this.authService.getMe().subscribe({
    next: (res) => {
      this.userData.name = res.name || '';
      this.userData.firstSurname = res.firstSurname || '';
      this.userData.secondSurname = res.secondSurname || ''; 
    }
  });
}

  loadTeacherData() {
    const teacherId = this.authService.getUserId();
    this.http.get<any>(`http://localhost:8081/teachers/${teacherId}`).subscribe({
      next: (res) => {
        this.teacherData.bio = res.bio || '';
        this.teacherData.pricePerHour = res.pricePerHour;
        this.teacherData.teachingLanguages = res.teachingLanguages || [];
        this.teacherData.spokenLanguages = res.spokenLanguages || [];
      }
    });
  }

  loadLanguages() {
    this.teacherService.getLanguages().subscribe({
      next: (res) => this.languages = res
    });
  }

  // ── GUARDAR DATOS PERSONALES ─────────────────────────
  saveUserData() {
    this.userErrors = {};
    if (!this.userData.name) this.userErrors['name'] = 'El nombre es obligatorio';
    if (!this.userData.firstSurname) this.userErrors['firstSurname'] = 'El apellido es obligatorio';
    if (Object.keys(this.userErrors).length > 0) return;

    this.userLoading = true;
    this.http.patch('http://localhost:8080/users/me', this.userData).subscribe({
      next: () => {
        this.userSuccess = true;
        this.userLoading = false;
        setTimeout(() => this.userSuccess = false, 3000);
      },
      error: () => {
        this.userErrors['general'] = 'Error guardando datos';
        this.userLoading = false;
      }
    });
  }

  // ── GUARDAR DATOS PROFESOR ───────────────────────────
  saveTeacherData() {
    this.teacherErrors = {};
    if (!this.teacherData.pricePerHour) {
      this.teacherErrors['price'] = 'El precio es obligatorio';
    }
    if (this.teacherData.teachingLanguages.length === 0) {
      this.teacherErrors['teaching'] = 'Selecciona al menos un idioma';
    }
    if (this.teacherData.spokenLanguages.length === 0) {
      this.teacherErrors['spoken'] = 'Selecciona al menos un idioma';
    }
    if (Object.keys(this.teacherErrors).length > 0) return;

    this.teacherLoading = true;
    const payload = {
      bio: this.teacherData.bio,
      pricePerHour: this.teacherData.pricePerHour,
      teachingLanguageIds: this.teacherData.teachingLanguages.map(l => l.id),
      spokenLanguageIds: this.teacherData.spokenLanguages.map(l => l.id)
    };

  this.http.patch('http://localhost:8081/teachers/me', payload).subscribe({
    next: () => {
      this.teacherSuccess = true;
      this.teacherLoading = false;
      setTimeout(() => this.teacherSuccess = false, 3000);
    },
    error: (err) => {
      this.teacherLoading = false;
      if (err.status === 400 && err.error && typeof err.error === 'object') {
        const map: Record<string, string> = {
          pricePerHour: 'price',
          teachingLanguageIds: 'teaching',
          spokenLanguageIds: 'spoken',
          bio: 'bio'
        };
        Object.entries(err.error).forEach(([field, msg]) => {
          const key = map[field] ?? field;
          this.teacherErrors[key] = msg as string;
        });
      } else {
        this.teacherErrors['general'] = 'Error guardando perfil';
      }
    }
  });
  }

  // ── IDIOMAS ──────────────────────────────────────────
  get availableTeachingLanguages() {
    return this.languages.filter(l =>
      !this.teacherData.teachingLanguages.find(s => s.id === l.id)
    );
  }

  get availableSpokenLanguages() {
    return this.languages.filter(l =>
      !this.teacherData.spokenLanguages.find(s => s.id === l.id)
    );
  }

  toggleTeaching() { this.showTeaching = !this.showTeaching; this.showSpoken = false; }
  toggleSpoken() { this.showSpoken = !this.showSpoken; this.showTeaching = false; }

  selectTeaching(lang: any) {
    this.teacherData.teachingLanguages.push(lang);
    delete this.teacherErrors['teaching'];
    this.showTeaching = false;
  }

  selectSpoken(lang: any) {
    this.teacherData.spokenLanguages.push(lang);
    delete this.teacherErrors['spoken'];
    this.showSpoken = false;
  }

  removeTeaching(lang: any) {
    this.teacherData.teachingLanguages = this.teacherData.teachingLanguages.filter(l => l.id !== lang.id);
  }

  removeSpoken(lang: any) {
    this.teacherData.spokenLanguages = this.teacherData.spokenLanguages.filter(l => l.id !== lang.id);
  }

  @HostListener('document:click')
  clickOutside() {
    this.showTeaching = false;
    this.showSpoken = false;
  }
}