import {GeneratedIdDirective} from './generated-id.directive';

describe('GeneratedIdDirective', () => {
  let elementSpy;
  let elementRef;
  let directive: GeneratedIdDirective;

  beforeEach(() => {
    elementSpy = jasmine.createSpyObj('element', ['setAttribute']);
    elementRef = {nativeElement: elementSpy};

    directive = new GeneratedIdDirective(elementRef);
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should generate an id', () => {
    directive.ngOnInit();
    expect(elementSpy.setAttribute).toHaveBeenCalledWith('id', jasmine.stringMatching(/^n[0-9]+$/));
  });

  it('should generate different ids for each Element', () => {
    directive.ngOnInit();
    const arg1 = elementSpy.setAttribute.calls.mostRecent().args[1];

    const directive2 = new GeneratedIdDirective(elementRef);
    directive2.ngOnInit();
    const arg2 = elementSpy.setAttribute.calls.mostRecent().args[1];

    expect(arg1).toMatch(/^n[0-9]+$/);
    expect(arg2).toMatch(/^n[0-9]+$/);
    expect(arg1).not.toEqual(arg2);
  });
});
