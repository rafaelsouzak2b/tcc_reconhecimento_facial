import { TestBed } from '@angular/core/testing';

import { FacialService } from './facial.service';

describe('FacialService', () => {
  let service: FacialService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FacialService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
