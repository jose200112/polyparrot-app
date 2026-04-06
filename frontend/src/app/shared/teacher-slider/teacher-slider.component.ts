import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TeacherService } from '../../features/teachers/services/teacher.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TeacherCardComponent } from '../../features/teachers/components/teacher-card/teacher-card.component';

@Component({
  selector: 'app-teacher-slider',
  standalone: true,
  imports: [CommonModule, TeacherCardComponent], 
  templateUrl: './teacher-slider.component.html',
  styleUrl: './teacher-slider.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TeacherSliderComponent {
  teachers: any[] = [];

  constructor(private teacherService: TeacherService) {}
  
  breakpoints = {
  640: { slidesPerView: 1 },
  768: { slidesPerView: 2 },
  1024: { slidesPerView: 3 },
  1280: { slidesPerView: 4 }
};

  ngOnInit() {
    this.teacherService.getAllTeachers().subscribe(
      data => {
        this.teachers = data;
      }
    )
  }
}
