import {AfterViewInit, Directive, ElementRef, Input} from '@angular/core';

@Directive({
  selector: '[autofocus]'
})
export class AutofocusDirective implements AfterViewInit {
  private doAutofocus = true;

  constructor(private el: ElementRef) {
  }

  ngAfterViewInit() {
    if (this.doAutofocus) {
      this.el.nativeElement.focus();
    }
  }

  @Input()
  set autofocus(condition: boolean) {
    this.doAutofocus = condition;
  }
}
