import {Directive, ElementRef, Input, OnInit} from '@angular/core';

@Directive({
  selector: '[autoFor]'
})
export class AutoForDirective implements OnInit {
  @Input('autoFor')
  referencedElement: HTMLElement;

  constructor(private targetElement: ElementRef) {
  }

  ngOnInit(): void {
    this.targetElement.nativeElement.setAttribute('for', this.referencedElement.getAttribute('id'));
  }
}
