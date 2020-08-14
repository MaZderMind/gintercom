import {ReferenceIdDirective} from 'src/app/utils/id-handling/reference-id';

const THE_ID = 'foobar';

describe('ReferenceIdDirective', () => {
  let targetElement: HTMLElement;
  let referencedElement: HTMLElement;
  let directive: ReferenceIdDirective;

  beforeEach(() => {
    targetElement = document.createElement('div');
    referencedElement = document.createElement('div');
    referencedElement.setAttribute('id', THE_ID);

    directive = new ReferenceIdDirective({nativeElement: targetElement});
    directive.referencedElement = referencedElement;
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should copy referenced id into for-attribute', () => {
    directive.ngOnInit();

    const forAttribute = targetElement.getAttribute('for');
    expect(forAttribute).toEqual(THE_ID);
  });

  it('should accept custom target attribute', () => {
    directive.attributeName = 'aria-labelledby';
    directive.ngOnInit();

    const ariaAttribute = targetElement.getAttribute('aria-labelledby');
    expect(ariaAttribute).toEqual(THE_ID);
  });
});
