import {Directive, ElementRef, HostBinding, Self} from '@angular/core';
import {NgControl} from '@angular/forms';

@Directive({
  selector: '[formControlName],[ngModel],[formControl]'
})
export class BootstrapValidateFieldAdapterDirective {
  constructor(
    private el: ElementRef,
    @Self() private control: NgControl
  ) {
  }

  @HostBinding('class.is-valid')
  get bootstrapIsValid(): boolean {
    return this.bootstrapValidationEnabled && this.control.touched && this.control.valid;
  }

  @HostBinding('class.is-invalid')
  get bootstrapIsInvalid(): boolean {
    return this.bootstrapValidationEnabled && this.control.touched && !this.control.valid;
  }

  get bootstrapValidationEnabled(): boolean {
    const el = this.el.nativeElement as HTMLElement;
    const form = el.closest('form');
    return form && form.hasAttribute('bootstrapValidate');
  }
}
