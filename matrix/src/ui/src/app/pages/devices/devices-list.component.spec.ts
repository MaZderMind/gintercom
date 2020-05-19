import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DevicesListComponent} from 'src/app/pages/devices/devices-list.component';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {instance, mock, when} from 'ts-mockito';

describe('DevicesListComponent', () => {
  let component: DevicesListComponent;
  let fixture: ComponentFixture<DevicesListComponent>;
  let devicesService: DevicesService;

  beforeEach(async(() => {
    devicesService = mock(DevicesService);
    when(devicesService.getOnlineDevices()).thenResolve([]);

    TestBed.configureTestingModule({
      declarations: [DevicesListComponent],
      providers: [
        {provide: DevicesService, useFactory: () => instance(devicesService)},
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
