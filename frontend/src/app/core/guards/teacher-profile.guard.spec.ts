import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { teacherProfileGuard } from './teacher-profile.guard';

describe('teacherProfileGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => teacherProfileGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
