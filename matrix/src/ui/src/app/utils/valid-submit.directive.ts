import {Directive, ElementRef, EventEmitter, HostListener, Output} from '@angular/core';
import {ControlContainer} from '@angular/forms';

@Directive({
  selector: '[validSubmit]'
})
export class ValidSubmitDirective {

  @Output()
  validSubmit = new EventEmitter();

  constructor(private el: ElementRef, private controlContainer: ControlContainer) {
    el.nativeElement.noValidate = true;
  }

  @HostListener('submit', ['$event'])
  onSubmit($event: UIEvent) {
    this.controlContainer.control.markAllAsTouched();

    if (this.controlContainer.valid) {
      this.validSubmit.emit($event);
    }
  }
}
