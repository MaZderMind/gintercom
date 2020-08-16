import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {AbstractSubForm} from 'src/app/components/abstract-sub-form';

@Component({
  selector: 'app-button-editor',
  templateUrl: './button-editor.component.html',
  styleUrls: ['./button-editor.component.scss'],
  providers: AbstractSubForm.providers(ButtonEditorComponent),
})
export class ButtonEditorComponent extends AbstractSubForm {
  public static readonly INITIAL_VALUE = {
    id: '',
    display: ''
  };

  buttonForm = new FormGroup({
    id: new FormControl(null, Validators.required),
    display: new FormControl('')
  });

  getSubForm(): AbstractControl {
    return this.buttonForm;
  }
}
