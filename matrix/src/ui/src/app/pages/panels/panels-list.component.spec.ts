import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';

describe('PanelsComponent', () => {
  let component: PanelsListComponent;
  let fixture: ComponentFixture<PanelsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PanelsListComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
