import {Component, ContentChild, Input, TemplateRef} from '@angular/core';
import {CdkDragDrop} from '@angular/cdk/drag-drop';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import * as _ from 'lodash';

@Component({
  selector: 'app-listgroup-multi-select',
  templateUrl: './listgroup-multi-select.component.html',
  styleUrls: ['./listgroup-multi-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: ListgroupMultiSelectComponent,
    multi: true
  }]
})
export class ListgroupMultiSelectComponent implements ControlValueAccessor {

  @Input()
  options: any[] = [];

  @Input()
  itemLabel = 'Item';

  @Input()
  valueGetter: (option: any) => any;

  @ContentChild(TemplateRef)
  labelTemplate: TemplateRef<any>;

  isDisabled = false;
  selectedValues = [];

  private onChange: any = () => null;
  private onTouched: any = () => null;

  onItemDrop(event: CdkDragDrop<any[]>) {
    const value = this.selectedValues.splice(event.previousIndex, 1)[0];
    this.selectedValues.splice(event.currentIndex, 0, value);
  }

  onItemToAddSelected(itemToAdd: any) {
    if (itemToAdd && !this.contains(itemToAdd)) {
      this.selectedValues.push(this.mapValue(itemToAdd));
      this.onChange(this.selectedValues);
      this.onTouched();
    }
  }

  contains(value: any): boolean {
    return this.selectedValues.includes(this.mapValue(value));
  }

  get availableOptions(): any[] {
    return this.options ? this.options.filter(option => !this.contains(option)) : [];

  }

  get selectedOptions(): any[] {
    if (!this.options) {
      return [];
    }

    const mappedValues = this.options.map(option => this.mapValue(option));
    return this.selectedValues.map(selectedValue => {
      const index = mappedValues.indexOf(selectedValue);
      return this.options[index];
    });
  }

  onDeleteClicked(option: any) {
    _.pull(this.selectedValues, this.mapValue(option));
    this.onChange(this.selectedValues);
    this.onTouched();
  }

  private mapValue(option: any) {
    return this.valueGetter ? this.valueGetter(option) : option;
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

  writeValue(selectedOptions: any[]): void {
    this.selectedValues = selectedOptions;
  }
}
