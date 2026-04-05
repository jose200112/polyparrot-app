import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-teacher-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './teacher-card.component.html',
  styleUrl: './teacher-card.component.css'
})
export class TeacherCardComponent {
  @Input() teacher: any;
}