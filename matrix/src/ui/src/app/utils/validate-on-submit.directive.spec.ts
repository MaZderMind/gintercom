import {ValidateOnSubmitDirective} from './validate-on-submit.directive';
import {ElementRef} from '@angular/core';

describe('ValidateOnSubmitDirective', () => {
  it('should create an instance', () => {
    const directive = new ValidateOnSubmitDirective(new ElementRef({}));
    expect(directive).toBeTruthy();
  });
});
