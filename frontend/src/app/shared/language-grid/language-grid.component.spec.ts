import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageGridComponent } from './language-grid.component';

describe('LanguageGridComponent', () => {
  let component: LanguageGridComponent;
  let fixture: ComponentFixture<LanguageGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LanguageGridComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LanguageGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
