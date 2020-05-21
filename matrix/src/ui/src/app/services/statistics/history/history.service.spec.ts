import {TestBed} from '@angular/core/testing';

import {HistoryService} from './history.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('HistoryService', () => {
  let service: HistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(HistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
