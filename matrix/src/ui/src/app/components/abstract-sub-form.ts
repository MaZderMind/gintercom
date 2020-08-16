import {AbstractControl, ControlValueAccessor, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator} from '@angular/forms';
import {ButtonEditDto} from 'src/app/services/panels/button-edit-dto';

export abstract class AbstractSubForm implements ControlValueAccessor, Validator {
  static providers(component: any) {
    return [{
      provide: NG_VALUE_ACCESSOR,
      useExisting: component,
      multi: true
    }, {
      provide: NG_VALIDATORS,
      useExisting: component,
      multi: true
    }];
  }

  private onTouched: any = () => void 0;

  abstract getSubForm(): AbstractControl;

  registerOnChange(fn: any): void {
    this.getSubForm().valueChanges.subscribe(fn);
    setTimeout(() => fn(this.getSubForm().value), 0);

  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.getSubForm().disable();
    } else {
      this.getSubForm().enable();
    }
  }

  writeValue(obj: ButtonEditDto[]): void {
    if (obj) {
      this.getSubForm().setValue(obj);
    } else {
      this.getSubForm().reset();
    }
  }

  propagateTouched() {
    this.onTouched();
  }

  validate(c: AbstractControl): ValidationErrors | null {
    return this.getSubForm().valid ? null : {invalidForm: {valid: false, message: 'fields are invalid'}};c
  }
}
