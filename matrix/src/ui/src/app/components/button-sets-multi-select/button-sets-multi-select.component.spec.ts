import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonSetsMultiSelectComponent } from './button-sets-multi-select.component';

describe('ButtonSetsMultiSelectComponent', () => {
  let component: ButtonSetsMultiSelectComponent;
  let fixture: ComponentFixture<ButtonSetsMultiSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ButtonSetsMultiSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonSetsMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
