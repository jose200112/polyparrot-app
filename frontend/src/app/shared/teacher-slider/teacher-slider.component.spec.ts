import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherSliderComponent } from './teacher-slider.component';

describe('TeacherSliderComponent', () => {
  let component: TeacherSliderComponent;
  let fixture: ComponentFixture<TeacherSliderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeacherSliderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TeacherSliderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
