import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (requiredRole: string): CanActivateFn => () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (authService.getRole() === requiredRole) return true;

  // Redirigir al home correspondiente si tiene otro rol
  const role = authService.getRole();
  if (role === 'TEACHER') router.navigate(['/teacher/home']);
  else if (role === 'STUDENT') router.navigate(['/student/home']);
  else router.navigate(['/']);
  return false;
};