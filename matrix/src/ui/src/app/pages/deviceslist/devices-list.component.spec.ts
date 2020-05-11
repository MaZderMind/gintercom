import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DevicesListComponent} from 'src/app/pages/deviceslist/devices-list.component';

describe('DevicesComponent', () => {
  let component: DevicesListComponent;
  let fixture: ComponentFixture<DevicesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DevicesListComponent]
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
