import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) return true;

  const role = authService.getRole();
  if (role === 'TEACHER') router.navigate(['/teacher/home']);
  else router.navigate(['/student/home']);
  return false;
};