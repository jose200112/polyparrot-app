import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeacherSetupComponent } from './teacher-setup.component';

describe('TeacherSetupComponent', () => {
  let component: TeacherSetupComponent;
  let fixture: ComponentFixture<TeacherSetupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeacherSetupComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TeacherSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
