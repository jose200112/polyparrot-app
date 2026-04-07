import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const studentOrGuestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // No logueado → puede acceder
  if (!authService.isLoggedIn()) return true;

  // STUDENT → puede acceder
  if (authService.getRole() === 'STUDENT') return true;

  // TEACHER → redirigir a su home
  router.navigate(['/teacher/home']);
  return false;
};