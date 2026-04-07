import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { catchError, map, of } from 'rxjs';

export const teacherSetupGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const http = inject(HttpClient);
  const router = inject(Router);

  if (!authService.isLoggedIn() || authService.getRole() !== 'TEACHER') {
    router.navigate(['/login']);
    return false;
  }

  const userId = authService.getUserId();
  return http.get(`http://localhost:8081/teachers/${userId}`).pipe(
    map(() => {
      router.navigate(['/teacher/home']); // ya tiene perfil → no puede ir a setup
      return false;
    }),
    catchError(() => of(true)) // no tiene perfil → puede ir a setup
  );
};