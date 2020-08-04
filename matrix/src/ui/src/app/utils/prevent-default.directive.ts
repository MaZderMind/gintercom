import {Directive, HostListener} from '@angular/core';

@Directive({
  selector: '[appPreventDefault]',
})
export class PreventDefaultDirective {
  @HostListener('click', ['$event'])
  onClick($event: MouseEvent) {
    $event.preventDefault();
  }
}
