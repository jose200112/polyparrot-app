import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  private apiUrl = 'http://localhost:8081/teachers'; 

  constructor(private http: HttpClient) {}

  getAllTeachers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  createTeacher(data: any) { 
    return this.http.post(this.apiUrl, data); 
  } 

  private cachedLanguages: any[] = [];

  getLanguages() { 
    return this.http.get<any[]>('http://localhost:8081/languages'); 
  }
}