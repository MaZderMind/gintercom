import {GeneratedIdDirective} from './generated-id.directive';

describe('GeneratedIdDirective', () => {
  let element: HTMLElement;
  let directive: GeneratedIdDirective;

  beforeEach(() => {
    element = document.createElement('div');
    directive = new GeneratedIdDirective({nativeElement: element});
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should generate an id', () => {
    directive.ngOnInit();
    expect(element.getAttribute('id')).toMatch(/^n[0-9]+$/);
  });

  it('should generate different ids for each Element', () => {
    const element1 = document.createElement('div');
    const directive1 = new GeneratedIdDirective({nativeElement: element1});
    directive1.ngOnInit();

    const element2 = document.createElement('div');
    const directive2 = new GeneratedIdDirective({nativeElement: element2});
    directive2.ngOnInit();

    const id1 = element1.getAttribute('id');
    const id2 = element2.getAttribute('id');
    expect(id1).toMatch(/^n[0-9]+$/);
    expect(id2).toMatch(/^n[0-9]+$/);

    expect(id1).not.toEqual(id2);
  });
});
