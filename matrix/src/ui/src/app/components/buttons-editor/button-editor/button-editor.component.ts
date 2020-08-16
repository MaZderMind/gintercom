import {Component, Input} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-button-editor',
  templateUrl: './button-editor.component.html',
  styleUrls: ['./button-editor.component.scss'],
})
export class ButtonEditorComponent {
  @Input()
  control: AbstractControl;

  get buttonForm(): FormGroup {
    return this.control as FormGroup;
  }

  static createControl(): AbstractControl {
    return new FormGroup({
      id: new FormControl(null, Validators.required),
      display: new FormControl('')
    });
  }
}
