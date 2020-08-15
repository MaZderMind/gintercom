import {Component} from '@angular/core';
import {ControlValueAccessor, FormArray, FormControl, FormGroup, NG_VALUE_ACCESSOR, Validators} from '@angular/forms';
import {ButtonEditDto} from 'src/app/services/panels/button-edit-dto';

@Component({
  selector: 'app-buttons-editor',
  templateUrl: './buttons-editor.component.html',
  styleUrls: ['./buttons-editor.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: ButtonsEditorComponent,
    multi: true
  }]
})
export class ButtonsEditorComponent implements ControlValueAccessor {
  private onChange: any;
  isDisabled: boolean;

  buttons = new FormArray([]);

  constructor() {
    this.buttons.valueChanges.subscribe(newValue => {
      if (this.onChange) {
        this.onChange(newValue);
      }
    });
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  writeValue(obj: ButtonEditDto[]): void {
    this.buttons.setValue(obj);
  }

  addButtonBottom() {
    const formGroup = new FormGroup({
      id: new FormControl('', Validators.required),
      display: new FormControl('')
    });

    this.buttons.push(formGroup);
  }

  // noinspection JSMethodCanBeStatic
}
