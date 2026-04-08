import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {

ngOnInit() {
  document.body.classList.add('auth-page');
}

ngOnDestroy() {
  document.body.classList.remove('auth-page');
}

frontErrors: any = {};
backError: string = '';

  user = {
    email: '',
    password: ''
  };

  error = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

login() {
  if (!this.validateForm()) return;

  this.backError = '';

  this.authService.login(this.user).subscribe({
    next: (res: any) => {
      this.authService.saveAuthData(res.token, res.role, res.userId, res.name);

      if (res.role === 'TEACHER') {
        this.http.get(`http://localhost:8081/teachers/${res.userId}`).subscribe({
          next: () => this.router.navigate(['/teacher/home']),
          error: () => this.router.navigate(['/teacher/setup'])
        });
      } else {
        this.router.navigate(['/student/home']);
      }
    },
    error: (err) => {
      this.backError = err.error?.error || 'Error inesperado';
    }
  });
}

  validateForm(): boolean {
  this.frontErrors = {};

  const emailError = this.validateEmail();
  if (emailError) this.frontErrors.email = emailError;

  const passwordError = this.validatePassword();
  if (passwordError) this.frontErrors.password = passwordError;

  return Object.keys(this.frontErrors).length === 0;
}

validateEmail(): string | null {
  if (!this.user.email) return 'El email es obligatorio';

  if (this.user.email.length > 100) return 'Máximo 100 caracteres';

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(this.user.email)) return 'El email no es válido';



  return null;
}

validatePassword(): string | null {
  if (!this.user.password) return 'Introduce la contraseña';

  if (this.user.password.length > 50) {
    return 'Máximo 50 caracteres';
  }

  return null;
}

onInputChange() {
  this.backError = '';
}


}