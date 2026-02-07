import { TestBed } from '@angular/core/testing';

import { DownloadReportService } from './download-report.service';

describe('DownloadReportService', () => {
  let service: DownloadReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DownloadReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
