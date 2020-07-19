import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelActionsComponent} from './panel-actions.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('PanelActionsComponent', () => {
  let component: PanelActionsComponent;
  let fixture: ComponentFixture<PanelActionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PanelActionsComponent],
      imports: [RouterTestingModule],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelActionsComponent);
    component = fixture.componentInstance;
    component.panel = {
      id: 'myPanel',
      display: 'My Panel',
      hostId: '0000-0000',
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
