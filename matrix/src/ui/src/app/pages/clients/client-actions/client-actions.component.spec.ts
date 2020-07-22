import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClientActionsComponent} from 'src/app/pages/clients/client-actions/client-actions.component';

describe('ionsComponent', () => {
  let component: ClientActionsComponent;
  let fixture: ComponentFixture<ClientActionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClientActionsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientActionsComponent);
    component = fixture.componentInstance;
    component.client = {
      clientId: '0000-0000',
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
