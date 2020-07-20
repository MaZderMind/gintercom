import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClientStatusComponent} from 'src/app/pages/clients/client-status/client-status.component';

describe('ClientStatusComponent', () => {
  let component: ClientStatusComponent;
  let fixture: ComponentFixture<ClientStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClientStatusComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientStatusComponent);
    component = fixture.componentInstance;
    component.client = {
      hostId: '0000-0000',
      panelId: 'somePanel',
      clientAddress: '127.0.0.1',
      firstSeen: '2020-01-01T10:00:00+0200',
      provisioned: true,
      clientModel: 'Test-Panel',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
