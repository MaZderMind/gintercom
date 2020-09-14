import {Component, OnInit, ViewChild} from '@angular/core';
import {ButtonSetDto} from 'src/app/services/button-sets/button-set-dto';
import {ButtonSetsService} from 'src/app/services/button-sets/button-sets.service';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {ListgroupMultiSelectComponent} from 'src/app/components/listgroup-multi-select/listgroup-multi-select.component';

@Component({
  selector: 'app-button-sets-multi-select',
  templateUrl: './button-sets-multi-select.component.html',
  styleUrls: ['./button-sets-multi-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: ButtonSetsMultiSelectComponent,
    multi: true
  }]
})
export class ButtonSetsMultiSelectComponent implements OnInit, ControlValueAccessor {
  @ViewChild(ListgroupMultiSelectComponent, {static: true})
  multiselect: ListgroupMultiSelectComponent;

  buttonSets: Array<ButtonSetDto>;

  constructor(private buttonSetsService: ButtonSetsService) {
  }

  ngOnInit(): void {
    this.buttonSetsService.getConfiguredButtonSets().then(buttonSets => {
      this.buttonSets = buttonSets;
    });
  }

  buttonSetValueGetter(buttonSet: ButtonSetDto) {
    return buttonSet.id;
  }

  registerOnChange(fn: any): void {
    this.multiselect.registerOnChange(fn);
  }

  registerOnTouched(fn: any): void {
    this.multiselect.registerOnTouched(fn);
  }

  setDisabledState(isDisabled: boolean): void {
    this.multiselect.setDisabledState(isDisabled);
  }

  writeValue(obj: string[]): void {
    this.multiselect.writeValue(obj);
  }
}
