import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AvailabilityService {

  private apiUrl = 'http://localhost:8081/availability';

  constructor(private http: HttpClient) {}

  getSlots(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}`);
  }

  createSlot(startTime: string): Observable<any> {
    return this.http.post(this.apiUrl, { startTime });
  }

  deleteSlot(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}