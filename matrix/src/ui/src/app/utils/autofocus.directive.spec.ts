import {AutofocusDirective} from 'src/app/utils/autofocus.directive';

describe('AutofocusDirective', () => {
  let elementSpy;
  let directive: AutofocusDirective;

  beforeEach(() => {
    elementSpy = jasmine.createSpyObj('element', ['focus']);
    directive = new AutofocusDirective({nativeElement: elementSpy});
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should call .focus() when autoFocus is true', () => {
    directive.autofocus = true;
    directive.ngAfterViewInit();

    expect(elementSpy.focus).toHaveBeenCalled();
  });

  it('should not call .focus() when autoFocus is false', () => {
    directive.autofocus = false;
    directive.ngAfterViewInit();

    expect(elementSpy.focus).not.toHaveBeenCalled();
  });
});
