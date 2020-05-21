import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelEditComponent} from './panel-edit.component';
import {ActivatedRoute} from '@angular/router';
import {EMPTY} from 'rxjs';

describe('PanelEditComponent', () => {
  let component: PanelEditComponent;
  let fixture: ComponentFixture<PanelEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PanelEditComponent],
      providers: [
        {
          provide: ActivatedRoute, useValue: {
            paramMap: EMPTY,
          }
        },
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
