import {Component, OnInit, ViewChild} from '@angular/core';
import {GroupDto} from 'src/app/services/groups/group-dto';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {CheckboxMultiSelectComponent} from 'src/app/components/checkbox-multi-select/checkbox-multi-select.component';

@Component({
  selector: 'app-group-multi-select',
  templateUrl: './group-multi-select.component.html',
  styleUrls: ['./group-multi-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: GroupMultiSelectComponent,
    multi: true
  }]
})
export class GroupMultiSelectComponent implements OnInit, ControlValueAccessor {
  @ViewChild(CheckboxMultiSelectComponent, {static: true})
  multiselect: CheckboxMultiSelectComponent;

  groups: Array<GroupDto>;

  constructor(private groupsService: GroupsService) {
  }

  ngOnInit(): void {
    this.groupsService.getConfiguredGroups().then(groups => {
      this.groups = groups;
    });
  }

  groupValueGetter(group: GroupDto) {
    return group.id;
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
