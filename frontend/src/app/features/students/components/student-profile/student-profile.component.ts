import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-student-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student-profile.component.html',
  styleUrl: './student-profile.component.css'
})
export class StudentProfileComponent implements OnInit, OnDestroy {

  userData = {
    name: '',
    firstSurname: '',
    secondSurname: ''
  };

  userErrors: Record<string, string> = {};
  userSuccess = false;
  userLoading = false;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit() {
    document.body.classList.add('student-layout');
    this.loadUserData();
  }

  ngOnDestroy() {
    document.body.classList.remove('student-layout');
  }

  loadUserData() {
    this.authService.getMe().subscribe({
      next: (res) => {
        this.userData.name = res.name || '';
        this.userData.firstSurname = res.firstSurname || '';
        this.userData.secondSurname = res.secondSurname || '';
      }
    });
  }

  saveUserData() {
    this.userErrors = {};
    if (!this.userData.name) this.userErrors['name'] = 'El nombre es obligatorio';
    if (!this.userData.firstSurname) this.userErrors['firstSurname'] = 'El apellido es obligatorio';
    if (Object.keys(this.userErrors).length > 0) return;

    this.userLoading = true;
    this.http.patch(`${environment.userServiceUrl}/users/me`, this.userData).subscribe({
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
}