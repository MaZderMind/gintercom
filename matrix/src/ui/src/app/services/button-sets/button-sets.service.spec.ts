import { TestBed } from '@angular/core/testing';

import { ButtonSetsService } from './button-sets.service';

describe('ButtonSetsService', () => {
  let service: ButtonSetsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ButtonSetsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
