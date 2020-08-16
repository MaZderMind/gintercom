import {Component} from '@angular/core';
import {AbstractControl, FormArray, FormControl, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AbstractSubForm} from 'src/app/components/abstract-sub-form';
import {ButtonEditorComponent} from 'src/app/components/buttons-editor/button-editor/button-editor.component';

@Component({
  selector: 'app-buttons-editor',
  templateUrl: './buttons-editor.component.html',
  styleUrls: ['./buttons-editor.component.scss'],
  providers: AbstractSubForm.providers(ButtonsEditorComponent),
})
export class ButtonsEditorComponent extends AbstractSubForm {
  buttons = new FormArray([]);

  addButtonBottom() {
    this.buttons.push(new FormControl());
  }

  getSubForm(): AbstractControl {
    return this.buttons;
  }
}
