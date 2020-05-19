import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DeviceStatusComponent} from './device-status.component';

describe('DeviceStatusComponent', () => {
  let component: DeviceStatusComponent;
  let fixture: ComponentFixture<DeviceStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DeviceStatusComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeviceStatusComponent);
    component = fixture.componentInstance;
    component.device = {
      hostId: '0000-0000',
      panelId: 'somePanel',
      remoteIp: '127.0.0.1',
      connectionTime: '2020-01-01T10:00:00+0200',
      provisioned: true,
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
