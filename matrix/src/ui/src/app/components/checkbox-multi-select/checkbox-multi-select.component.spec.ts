import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckboxMultiSelectComponent } from './checkbox-multi-select.component';

describe('CheckboxMultiSelectComponent', () => {
  let component: CheckboxMultiSelectComponent;
  let fixture: ComponentFixture<CheckboxMultiSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckboxMultiSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckboxMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
