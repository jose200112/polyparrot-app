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

export const routes: Routes = [

  {
    path: '',
    component: HomeComponent
  },

  {
    path: 'teacher/setup',
    component: TeacherSetupComponent
  },

  {
  path: 'teachers',
  component: TeacherSearchComponent
  },

  {
  path: 'teacher/home',
  component: HomeTeacherComponent
  },

  {
    path: 'teacher/calendar',
    component: TeacherCalendarComponent
  },

  {
  path: 'teacher/profile',
  component: TeacherProfileComponent
  },

  {
    path: 'student/home',
    component: HomeStudentComponent
  },

  {
  path: 'student/calendar',
  component: StudentCalendarComponent
  },

  {
  path: 'student/profile',
  component: StudentProfileComponent
  },

  {
    path: 'register',
    component: RegisterComponent
  },

  {
    path: 'login',
    component: LoginComponent
  },

  {
  path: 'notifications',
  component: NotificationsComponent
  },

  {
    path: '**',
    redirectTo: ''
  }
];