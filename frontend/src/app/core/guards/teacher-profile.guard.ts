import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export const teacherProfileGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const http = inject(HttpClient);
  const router = inject(Router);

  if (!authService.isLoggedIn() || authService.getRole() !== 'TEACHER') {
    router.navigate(['/login']);
    return false;
  }

  const userId = authService.getUserId();
  return http.get(`${environment.teacherServiceUrl}/teachers/${userId}`).pipe(
    map(() => true), // tiene perfil → puede acceder
    catchError(() => {
      router.navigate(['/teacher/setup']); // no tiene perfil → setup
      return of(false);
    })
  );
};