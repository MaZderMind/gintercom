import {TestBed} from '@angular/core/testing';

import {DevicesService} from './devices.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('DevicesService', () => {
  let service: DevicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(DevicesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
