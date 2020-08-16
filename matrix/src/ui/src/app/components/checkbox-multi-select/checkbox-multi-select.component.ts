import {Component, ContentChild, Input, TemplateRef} from '@angular/core';
import {ControlContainer, ControlValueAccessor, NG_VALUE_ACCESSOR, NgControl} from '@angular/forms';
import * as _ from 'lodash';

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
  valueGetter: (option: any) => any;

  @ContentChild(TemplateRef)
  labelTemplate: TemplateRef<any>;

  private onChange: any;
  isDisabled = false;

  private selectedOptions = [];
  private onTouched: any = () => null;

  constructor(private control: NgControl) {
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  writeValue(obj: any[]): void {
    this.selectedOptions = obj;
  }

  onCheckboxChange($event: Event, option: any) {
    const checked = ($event.target as HTMLInputElement).checked;
    const value = this.valueGetter ? this.valueGetter(option) : option;

    if (checked) {
      this.selectedOptions.push(value);
    } else {
      _.pull(this.selectedOptions, value);
    }
    this.onChange(this.selectedOptions);
    this.onTouched();
  }

  isOptionActive(option: any) {
    const value = this.valueGetter ? this.valueGetter(option) : option;

    return this.selectedOptions.includes(value);
  }

  get showValid() {
    return this.control.touched && this.control.valid;
  }

  get showInvalid() {
    return this.control.touched && !this.control.valid;
  }
}
