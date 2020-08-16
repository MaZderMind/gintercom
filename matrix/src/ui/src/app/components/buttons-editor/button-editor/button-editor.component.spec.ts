import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonEditorComponent } from './button-editor.component';

describe('ButtonEditorComponent', () => {
  let component: ButtonEditorComponent;
  let fixture: ComponentFixture<ButtonEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ButtonEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
