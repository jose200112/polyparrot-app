import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { studentOrGuestGuard } from './student-or-guest.guard';

describe('studentOrGuestGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => studentOrGuestGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
