import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DevicesListComponent} from 'src/app/pages/devices/devices-list.component';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {ActivatedRoute} from '@angular/router';
import {of, Subscription} from 'rxjs';
import {UiUpdateService} from 'src/app/services/ui-update.service';

describe('DevicesListComponent', () => {
  let component: DevicesListComponent;
  let fixture: ComponentFixture<DevicesListComponent>;
  let devicesService: DevicesService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    devicesService = mock(DevicesService);
    when(devicesService.getOnlineDevices()).thenResolve([]);

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [DevicesListComponent],
      providers: [
        {provide: DevicesService, useFactory: () => instance(devicesService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of({get: () => undefined}),
          }
        },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DevicesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
