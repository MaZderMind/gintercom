import {Directive, ElementRef, OnInit} from '@angular/core';

@Directive({
  selector: '[generatedId]'
})
export class GeneratedIdDirective implements OnInit {
  private static counter = 0;

  constructor(private el: ElementRef) {
  }

  ngOnInit(): void {
    const generatedId = 'n' + (GeneratedIdDirective.counter++);
    this.el.nativeElement.setAttribute('id', generatedId);
  }
}
