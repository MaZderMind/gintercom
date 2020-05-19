import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DeviceActionsComponent} from './device-actions.component';

describe('DeviceActionsComponent', () => {
  let component: DeviceActionsComponent;
  let fixture: ComponentFixture<DeviceActionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DeviceActionsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeviceActionsComponent);
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
