import { TestBed } from '@angular/core/testing';

import { IdendityService } from './idendity.service';

describe('IdendityService', () => {
  let service: IdendityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IdendityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
