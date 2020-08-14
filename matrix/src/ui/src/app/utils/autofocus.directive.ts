import {AfterViewInit, Directive, ElementRef, Input} from '@angular/core';

@Directive({
  selector: '[autofocus]'
})
export class AutofocusDirective implements AfterViewInit {
  @Input()
  autofocus = true;

  constructor(private el: ElementRef) {
  }

  ngAfterViewInit() {
    if (this.autofocus) {
      this.el.nativeElement.focus();
    }
  }
}
