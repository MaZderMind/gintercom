import {Component, Input} from '@angular/core';
import {AbstractControl, FormArray} from '@angular/forms';
import {ButtonEditorComponent} from 'src/app/pages/panels/components/buttons-editor/button-editor/button-editor.component';
import * as _ from 'lodash';

@Component({
  selector: 'app-buttons-editor',
  templateUrl: './buttons-editor.component.html',
  styleUrls: ['./buttons-editor.component.scss'],
})
export class ButtonsEditorComponent {
  @Input()
  control: AbstractControl;

  get buttons(): FormArray {
    return this.control as FormArray;
  }

  static createControl(): AbstractControl {
    return new FormArray([]);
  }

  addButton() {
    this.buttons.push(ButtonEditorComponent.createControl(this.buttons.length + 1));
  }

  onDelete(index: number) {
    _.pullAt(this.buttons.controls, index);
  }
}