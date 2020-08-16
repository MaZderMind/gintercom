import {Directive, HostBinding, Self} from '@angular/core';
import {NgControl} from '@angular/forms';

@Directive({
  selector: '[formControlName],[ngModel],[formControl]'
})
export class BootstrapFormValidationFieldAdapterDirective {
  private control: NgControl;

  constructor(@Self() control: NgControl) {
    this.control = control;
  }

  @HostBinding('class.is-valid')
  get bootstrapIsValid(): boolean {
    return this.control.touched && this.control.valid;
  }

  @HostBinding('class.is-invalid')
  get bootstrapIsInvalid(): boolean {
    return this.control.touched && !this.control.valid;
  }
}
