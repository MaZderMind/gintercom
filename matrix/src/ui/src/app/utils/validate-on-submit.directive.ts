import {Directive, ElementRef, EventEmitter, HostListener, Output} from '@angular/core';

@Directive({
  selector: '[validateOnSubmit]'
})
export class ValidateOnSubmitDirective {

  @Output()
  validSubmit = new EventEmitter();

  constructor(private el: ElementRef) {
    el.nativeElement.noValidate = true;
  }

  @HostListener('submit', ['$event'])
  onSubmit($event: UIEvent) {
    const classList: DOMTokenList = this.el.nativeElement.classList;
    classList.add('was-validated');

    if (!classList.contains('ng-invalid')) {
      this.validSubmit.emit($event);
    }
  }
}
