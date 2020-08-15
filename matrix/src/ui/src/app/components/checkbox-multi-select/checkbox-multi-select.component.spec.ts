import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CheckboxMultiSelectComponent} from './checkbox-multi-select.component';
import {GeneratedIdDirective} from 'src/app/utils/id-handling/generated-id.directive';
import {ReferenceIdDirective} from 'src/app/utils/id-handling/reference-id';
import {Component, ViewChild} from '@angular/core';

class Page {
  constructor(private fixture: ComponentFixture<any>) {
  }

  get checkboxes(): HTMLInputElement[] {
    return Array.from(this.fixture.nativeElement.querySelectorAll('input[type=checkbox]'));
  }

  get checkboxSelection(): boolean[] {
    return this.checkboxes.map(checkbox => checkbox.checked);
  }

  get labels(): HTMLInputElement[] {
    return Array.from(this.fixture.nativeElement.querySelectorAll('label'));
  }

  get labelTexts(): string[] {
    return this.labels.map(label => label.innerText.trim());
  }
}

class ExamplePerson {
  constructor(private id: number, private firstName: string, private lastName: string) {
  }

  get fullName(): string {
    return this.firstName + ' ' + this.lastName;
  }

  toString() {
    return this.fullName;
  }
}

@Component({
  template: `
    <app-checkbox-multi-select>
      <ng-template let-person>
        #{{ person.id }} {{ person.lastName }}, {{ person.firstName }}
      </ng-template>
    </app-checkbox-multi-select>
  `,
})
class WrapperComponent {
  @ViewChild(CheckboxMultiSelectComponent)
  componentUnderTest: CheckboxMultiSelectComponent;
}

describe('CheckboxMultiSelectComponent', () => {
  let component: CheckboxMultiSelectComponent;
  let fixture: ComponentFixture<CheckboxMultiSelectComponent>;
  let page: Page;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        WrapperComponent,
        CheckboxMultiSelectComponent,
        GeneratedIdDirective,
        ReferenceIdDirective
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckboxMultiSelectComponent);
    page = new Page(fixture);

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('general operation', () => {
    beforeEach(() => {
      component.options = ['foo', 'bar', 'moo'];
      fixture.detectChanges();
    });
    it('should disable checkboxes if requested to', () => {
      page.checkboxes.forEach(checkbox => {
        expect(checkbox.disabled).toBeFalse();
      });

      component.setDisabledState(true);
      fixture.detectChanges();

      page.checkboxes.forEach(checkbox => {
        expect(checkbox.disabled).toBeTrue();
      });

      component.setDisabledState(false);
      fixture.detectChanges();

      page.checkboxes.forEach(checkbox => {
        expect(checkbox.disabled).toBeFalse();
      });
    });

    it('should generate options', () => {
      expect(page.checkboxes.length).toEqual(3);
      expect(page.labels.length).toEqual(3);
    });

    it('should link labels and checkboxes', () => {
      const labels = page.labels;
      page.checkboxes.forEach((checkbox, index) => {
        expect(checkbox.id.length).not.toEqual(0);
        expect(labels[index].getAttribute('for')).toEqual(checkbox.id);
      });
    });

    it('initial selection should be unchecked', () => {
      page.checkboxes.forEach(checkbox => {
        expect(checkbox.checked).toBeFalse();
      });
    });
  });

  describe('operation with native values', () => {
    beforeEach(() => {
      component.options = ['foo', 'bar', 'moo'];
      fixture.detectChanges();
    });

    it('should generate labels', () => {
      expect(page.labelTexts).toEqual(['foo', 'bar', 'moo']);
    });

    it('should accept value update', () => {
      expect(page.checkboxSelection).toEqual([false, false, false]);

      component.writeValue(['moo', 'bar']);
      fixture.detectChanges();

      expect(page.checkboxSelection).toEqual([false, true, true]);
    });

    it('should update selection on click', () => {
      let value: string[];
      component.registerOnChange((newValue => value = newValue));

      page.checkboxes[1].click();

      expect(value).toEqual(['bar']);
    });
  });

  describe('operation with self-labeled objects', () => {
    let persons: ExamplePerson[];
    beforeEach(() => {
      persons = [
        new ExamplePerson(0, 'John', 'Doe'),
        new ExamplePerson(4, 'Jane', 'Doe'),
        new ExamplePerson(6, 'Johnny', 'Doe'),
      ];
      component.options = persons;
      fixture.detectChanges();
    });

    it('should generate labels', () => {
      expect(page.labelTexts).toEqual(['John Doe', 'Jane Doe', 'Johnny Doe']);
    });

    it('should accept value update', () => {
      expect(page.checkboxSelection).toEqual([false, false, false]);

      component.writeValue([persons[1], persons[2]]);
      fixture.detectChanges();

      expect(page.checkboxSelection).toEqual([false, true, true]);
    });

    it('should update selection on click', () => {
      let value: ExamplePerson[];
      component.registerOnChange((newValue => value = newValue));

      page.checkboxes[1].click();

      expect(value.length).toEqual(1);
      expect(value[0]).toBe(persons[1]);
    });
  });

  describe('operation with custom value-getter', () => {
    let persons: ExamplePerson[];
    beforeEach(() => {
      persons = [
        new ExamplePerson(0, 'John', 'Doe'),
        new ExamplePerson(4, 'Jane', 'Doe'),
        new ExamplePerson(6, 'Johnny', 'Doe'),
      ];
      component.options = persons;
      component.valueGetter = (v: ExamplePerson) => v.fullName;
      fixture.detectChanges();
    });

    it('should generate labels', () => {
      expect(page.labelTexts).toEqual(['John Doe', 'Jane Doe', 'Johnny Doe']);
    });

    it('should accept value update', () => {
      expect(page.checkboxSelection).toEqual([false, false, false]);

      component.writeValue(['Jane Doe', 'Johnny Doe']);
      fixture.detectChanges();

      expect(page.checkboxSelection).toEqual([false, true, true]);
    });

    it('should update selection on click', () => {
      let value: string[];
      component.registerOnChange((newValue => value = newValue));

      page.checkboxes[1].click();

      expect(value).toEqual(['Jane Doe']);
    });
  });

  describe('with custom template', () => {
    let persons: ExamplePerson[];
    beforeEach(() => {
      persons = [
        new ExamplePerson(0, 'John', 'Doe'),
        new ExamplePerson(4, 'Jane', 'Doe'),
        new ExamplePerson(6, 'Johnny', 'Doe'),
      ];

      const wrapperFixture = TestBed.createComponent(WrapperComponent);
      wrapperFixture.detectChanges();
      page = new Page(wrapperFixture);

      component = wrapperFixture.componentInstance.componentUnderTest;
      component.options = persons;
      wrapperFixture.detectChanges();

    });

    it('should generate custom labels', () => {
      expect(page.labelTexts).toEqual(['#0 Doe, John', '#4 Doe, Jane', '#6 Doe, Johnny']);
    });
  });
});
