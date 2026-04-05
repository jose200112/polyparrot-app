import { Component } from '@angular/core';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { HeroComponent } from '../../shared/hero/hero.component';
import { TeacherSliderComponent } from '../../shared/teacher-slider/teacher-slider.component';
import { LanguageGridComponent } from '../../shared/language-grid/language-grid.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    NavbarComponent,
    HeroComponent,
    TeacherSliderComponent,
    LanguageGridComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
