import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[referenceId]'
})
export class ReferenceIdDirective implements OnInit {
  @Input('referenceId')
  referencedElement: HTMLElement;

  @Input()
  attributeName = 'for';

  constructor(private targetElement: ElementRef) {
  }

  ngOnInit(): void {
    this.targetElement.nativeElement.setAttribute(this.attributeName,
      this.referencedElement.getAttribute('id'));
  }
}
