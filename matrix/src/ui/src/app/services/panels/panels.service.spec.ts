import {TestBed} from '@angular/core/testing';

import {PanelsService} from './panels.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('PanelsService', () => {
  let service: PanelsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(PanelsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
