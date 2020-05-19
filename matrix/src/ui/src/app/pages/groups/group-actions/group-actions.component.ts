import {Component, Input} from '@angular/core';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Component({
  selector: 'app-group-actions',
  templateUrl: './group-actions.component.html',
  styleUrls: ['./group-actions.component.scss']
})
export class GroupActionsComponent {
  @Input()
  group: GroupDto;

  deleteGroup() {
  }
}
