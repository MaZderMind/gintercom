import {AfterViewInit, Directive, ElementRef, Input} from '@angular/core';

@Directive({
  selector: '[referenceId]'
})
export class ReferenceIdDirective implements AfterViewInit {
  @Input('referenceId')
  referencedElement: HTMLElement;

  @Input()
  attributeName = 'for';

  constructor(private targetElement: ElementRef) {
  }

  ngAfterViewInit(): void {
    this.targetElement.nativeElement.setAttribute(this.attributeName,
      this.referencedElement.getAttribute('id'));
  }
}
