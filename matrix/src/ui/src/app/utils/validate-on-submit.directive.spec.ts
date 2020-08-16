import {ValidSubmitDirective} from 'src/app/utils/valid-submit.directive';
import {ElementRef} from '@angular/core';

describe('ValidateOnSubmitDirective', () => {
  it('should create an instance', () => {
    const directive = new ValidSubmitDirective(new ElementRef({}));
    expect(directive).toBeTruthy();
  });
});
