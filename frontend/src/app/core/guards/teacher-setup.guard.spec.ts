import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { teacherSetupGuard } from './teacher-setup.guard';

describe('teacherSetupGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => teacherSetupGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
