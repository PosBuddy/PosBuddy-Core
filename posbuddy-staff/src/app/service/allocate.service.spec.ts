import { TestBed } from '@angular/core/testing';

import { AllocateService } from './allocate.service';

describe('AllocateService', () => {
  let service: AllocateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AllocateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
