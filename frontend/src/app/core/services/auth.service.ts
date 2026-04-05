import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth'; // 🔥 ajusta backend

  constructor(private http: HttpClient) { }

  saveAuthData(token: string, role: string, userId: number) { 
    localStorage.setItem('token', token); 
    localStorage.setItem('role', role); 
    localStorage.setItem('userId', userId.toString()); 
  }

  getUserId(): string | null {
    return localStorage.getItem('userId');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }


  // 🔹 OBTENER TOKEN
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // 🔹 REGISTER
  registerStudent(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register/student`, data);
  }

  // 🔹 REGISTER
  registerTeacher(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register/teacher`, data);
  }

  // 🔹 LOGIN
  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data);
  }

  // 🔹 GUARDAR TOKEN
  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  // 🔹 LOGOUT
logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('userId');
}

  // 🔹 ESTÁ LOGUEADO
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getMe(){
    return this.http.get<any>('http://localhost:8080/users/me');
  }
}