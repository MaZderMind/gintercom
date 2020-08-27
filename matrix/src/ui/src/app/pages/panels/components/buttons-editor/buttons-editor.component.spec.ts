import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ButtonsEditorComponent} from './buttons-editor.component';
import {MockComponent} from 'ng-mocks';
import {ButtonEditorComponent} from 'src/app/pages/panels/components/buttons-editor/button-editor/button-editor.component';

describe('ButtonsEditorComponent', () => {
  let component: ButtonsEditorComponent;
  let fixture: ComponentFixture<ButtonsEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ButtonsEditorComponent,
        MockComponent(ButtonEditorComponent),
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonsEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
