import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelStatusComponent} from './panel-status.component';

describe('PanelStatusComponent', () => {
  let component: PanelStatusComponent;
  let fixture: ComponentFixture<PanelStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PanelStatusComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelStatusComponent);
    component = fixture.componentInstance;
    component.panel = {
      id: 'myPanel',
      display: 'My Panel',
      clientId: '0000-0000',
      assigned: true,
      online: false,
      clientModel: 'Test-Panel',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
