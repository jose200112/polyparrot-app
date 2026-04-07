import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginComponent } from './features/auth/login/login.component';
import { HomeStudentComponent } from './pages/home-student/home-student.component';
import { TeacherSetupComponent } from './features/teachers/components/teacher-setup/teacher-setup.component';
import { TeacherCalendarComponent } from './features/teachers/components/teacher-calendar/teacher-calendar.component';
import { TeacherSearchComponent } from './features/teachers/components/teacher-search/teacher-search.component';
import { HomeTeacherComponent } from './pages/home-teacher/home-teacher.component';
import { TeacherProfileComponent } from './features/teachers/components/teacher-profile/teacher-profile.component';
import { StudentCalendarComponent } from './features/students/components/student-calendar/student-calendar.component';
import { StudentProfileComponent } from './features/students/components/student-profile/student-profile.component';
import { NotificationsComponent } from './shared/notifications/notifications.component';
import { roleGuard } from './core/guards/role.guard';
import { authGuard } from './core/guards/auth.guard';
import { teacherSetupGuard } from './core/guards/teacher-setup.guard';
import { teacherProfileGuard } from './core/guards/teacher-profile.guard';
import { guestGuard } from './core/guards/guest.guard';
import { studentOrGuestGuard } from './core/guards/student-or-guest.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent, canActivate: [guestGuard] },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: 'teachers', component: TeacherSearchComponent, canActivate: [studentOrGuestGuard] },

  // Solo STUDENT
  { path: 'student/home', component: HomeStudentComponent, canActivate: [roleGuard('STUDENT')] },
  { path: 'student/calendar', component: StudentCalendarComponent, canActivate: [roleGuard('STUDENT')] },
  { path: 'student/profile', component: StudentProfileComponent, canActivate: [roleGuard('STUDENT')] },

  // Setup — solo profesores SIN perfil
  { path: 'teacher/setup', component: TeacherSetupComponent, canActivate: [teacherSetupGuard] },

  // Rutas de profesor — solo profesores CON perfil
  { path: 'teacher/home', component: HomeTeacherComponent, canActivate: [teacherProfileGuard] },
  { path: 'teacher/calendar', component: TeacherCalendarComponent, canActivate: [teacherProfileGuard] },
  { path: 'teacher/profile', component: TeacherProfileComponent, canActivate: [teacherProfileGuard] },

  // Autenticado cualquier rol
  { path: 'notifications', component: NotificationsComponent, canActivate: [authGuard] },

  { path: '**', redirectTo: '' }
];