import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent implements OnInit, OnDestroy {

  user = {
    name: '',
    firstSurname: '',
    secondSurname: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

backErrors: Record<string, string> = {};
frontErrors: Record<string, string> = {};

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  type: 'student' | 'teacher' = 'student';

ngOnInit() {
  document.body.classList.add('auth-page');

  this.route.queryParams
    .pipe(take(1))
    .subscribe(params => {
      const type = params['type'];
      this.type = type === 'teacher' ? 'teacher' : 'student';
    });
}

  ngOnDestroy() {
    document.body.classList.remove('auth-page');
  }



register() {

  if (!this.validateForm()) return;

  this.backErrors = {};

  const payload = {
    name: this.user.name,
    firstSurname: this.user.firstSurname,
    secondSurname: this.user.secondSurname,
    email: this.user.email,
    password: this.user.password
  };

  const request$ = this.type === 'teacher'
    ? this.authService.registerTeacher(payload)
    : this.authService.registerStudent(payload);

  request$.subscribe({
    next: () => this.router.navigate(['/login']),
error: (err) => {
  if (err.error && typeof err.error === 'object') {
    this.backErrors = err.error;
  } else {
    this.backErrors = { general: err.error?.error || 'Error inesperado' };
  }

}
  });
}

validateForm(): boolean {

  this.frontErrors = {};

  const nameError = this.validateName();
  if (nameError) this.frontErrors['name'] = nameError;

  const firstSurnameError = this.validateFirstSurname();
  if (firstSurnameError) this.frontErrors['firstSurname' ]= firstSurnameError;

  const secondSurnameError = this.validateSecondSurname();
  if (secondSurnameError) this.frontErrors['secondSurname'] = secondSurnameError;

  const emailError = this.validateEmail();
  if (emailError) this.frontErrors['email'] = emailError;

  const passwordError = this.validatePassword();
  if (passwordError) this.frontErrors['password'] = passwordError;

  const confirmError = this.validateConfirmPassword();
  if (confirmError) this.frontErrors['confirmPassword'] = confirmError;

  return Object.keys(this.frontErrors).length === 0;
}

validateName(): string | null {
  if (!this.user.name) return 'Introduce tu nombre';
  if (this.user.name.length > 50) return 'Máximo 50 caracteres';
  return null;
}

validateFirstSurname(): string | null {
  if (!this.user.firstSurname) return 'Introduce tu primer apellido';
  if (this.user.firstSurname.length > 50) return 'Máximo 50 caracteres';
  return null;
}

validateSecondSurname(): string | null {
  if (this.user.secondSurname && this.user.secondSurname.length > 50) {
    return 'Máximo 50 caracteres';
  }
  return null;
}

validateEmail(): string | null {
  if (!this.user.email) return 'El email es obligatorio';

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(this.user.email)) return 'El email no es válido';

  if (this.user.email.length > 100) return 'Máximo 100 caracteres';

  return null;
}

validatePassword(): string | null {
  if (!this.user.password) return 'Introduce una contraseña';

  if (this.user.password.length < 6 || this.user.password.length > 50) {
    return 'La contraseña debe tener entre 6 y 50 caracteres';
  }

  const regex = /^(?=.*[0-9])(?=.*[_\-!@#$%^&*]).{6,50}$/;
  if (!regex.test(this.user.password)) {
    return 'Debe incluir al menos un número y un carácter especial';
  }

  return null;
}

validateConfirmPassword(): string | null {
  if (!this.user.confirmPassword) return 'Confirma la contraseña';

  if (this.user.password !== this.user.confirmPassword) {
    return 'Las contraseñas no coinciden';
  }

  return null;
}

onInputChange(field: string) {
  delete this.backErrors[field];
  delete this.backErrors['general'];
}

}