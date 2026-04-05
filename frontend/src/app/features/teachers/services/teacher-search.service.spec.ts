import { TestBed } from '@angular/core/testing';

import { TeacherSearchService } from './teacher-search.service';

describe('TeacherSearchService', () => {
  let service: TeacherSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TeacherSearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
