import {Directive, HostListener} from '@angular/core';

@Directive({
  selector: '[preventDefault]',
})
export class PreventDefaultDirective {
  @HostListener('click', ['$event'])
  onClick($event: MouseEvent) {
    $event.preventDefault();
  }
}
