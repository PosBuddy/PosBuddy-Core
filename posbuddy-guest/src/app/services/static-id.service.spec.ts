import { TestBed } from '@angular/core/testing';

import { StaticIdService } from './static-id.service';

describe('StaticIdService', () => {
  let service: StaticIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StaticIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
