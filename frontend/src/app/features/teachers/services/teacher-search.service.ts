import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TeacherSearchService {

  private apiUrl = 'http://localhost:8081/teachers/search';

  constructor(private http: HttpClient) {}

  searchTeachers(filters: any): Observable<any[]> {
    const params: any = {};
    if (filters.minPrice != null) params.minPrice = filters.minPrice;
    if (filters.maxPrice != null) params.maxPrice = filters.maxPrice;
    if (filters.teachingLanguage) params.teachingLanguage = filters.teachingLanguage;
    if (filters.spokenLanguage) params.spokenLanguage = filters.spokenLanguage;
    if (filters.startTime) params.startTime = filters.startTime;
    if (filters.sortOrder) params.sortOrder = filters.sortOrder;

    return this.http.get<any[]>(this.apiUrl, { params });
  }
}