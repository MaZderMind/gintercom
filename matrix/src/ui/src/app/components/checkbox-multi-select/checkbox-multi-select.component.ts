import {Component, ContentChild, Input, TemplateRef} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import * as lodash from 'lodash';

@Component({
  selector: 'app-checkbox-multi-select',
  templateUrl: './checkbox-multi-select.component.html',
  styleUrls: ['./checkbox-multi-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: CheckboxMultiSelectComponent,
    multi: true
  }]
})
export class CheckboxMultiSelectComponent implements ControlValueAccessor {

  @Input()
  options: any[] = [];

  @Input()
  equalityPredicate: (a: any, b: any) => boolean;

  @Input()
  valueGetter: (option: any) => any;

  @ContentChild(TemplateRef)
  labelTemplate: TemplateRef<any>;

  private onChange: any;
  private selectedOptions = [];

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
  }

  writeValue(obj: any): void {
    this.selectedOptions = obj;
  }

  onCheckboxChange($event: Event, option: any) {
    const checked = ($event.target as HTMLInputElement).checked;
    const value = this.valueGetter ? this.valueGetter(option) : option;

    if (checked) {
      this.selectedOptions.push(value);
    } else {
      lodash.pull(this.selectedOptions, value);
    }
    this.onChange(this.selectedOptions);
  }

  isOptionActive(option: any) {
    const value = this.valueGetter ? this.valueGetter(option) : option;

    return this.equalityPredicate ?
      this.selectedOptions.some(v => this.equalityPredicate(v, value)) :
      this.selectedOptions.includes(value);
  }
}
