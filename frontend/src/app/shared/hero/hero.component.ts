import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css'
})
export class HeroComponent implements OnInit {

  languages: any[] = [];
  selectedLanguage: any = null;
  showDropdown = false;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.http.get<any[]>(`${environment.teacherServiceUrl}/languages`).subscribe({
      next: (res) => this.languages = res,
      error: () => {}
    });
  }

  selectLanguage(lang: any) {
    this.selectedLanguage = lang;
    this.showDropdown = false;
  }

  clearSelection() {
    this.selectedLanguage = null;
  }

  search() {
    if (this.selectedLanguage) {
      this.router.navigate(['/teachers'], {
        queryParams: { teachingLanguage: this.selectedLanguage.name }
      });
    } else {
      this.router.navigate(['/teachers']);
    }
  }

  @HostListener('document:click')
  clickOutside() {
    this.showDropdown = false;
  }
}