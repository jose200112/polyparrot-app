import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-language-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-grid.component.html',
  styleUrl: './language-grid.component.css'
})
export class LanguageGridComponent implements OnInit {

languages = [
  { name: 'inglés', apiName: 'Inglés', icon: 'assets/icons/united-kingdom.png', count: 0 },
  { name: 'español', apiName: 'Español', icon: 'assets/icons/spain.png', count: 0 },
  { name: 'francés', apiName: 'Francés', icon: 'assets/icons/france.png', count: 0 },
  { name: 'alemán', apiName: 'Alemán', icon: 'assets/icons/germany.png', count: 0 },
  { name: 'italiano', apiName: 'Italiano', icon: 'assets/icons/italy.png', count: 0 },
  { name: 'japonés', apiName: 'Japonés', icon: 'assets/icons/japan.png', count: 0 }
];

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadCounts();
  }

  loadCounts() {
    this.languages.forEach(lang => {
      this.http.get<any[]>(`${environment.teacherServiceUrl}/teachers/search?teachingLanguage=${lang.apiName}`)
        .subscribe({
          next: (res) => lang.count = res.length,
          error: () => {}
        });
    });
  }

  goToSearch(lang: any) {
    this.router.navigate(['/teachers'], {
      queryParams: { teachingLanguage: lang.apiName }
    });
  }
}