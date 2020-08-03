import {Component, Input} from '@angular/core';
import {GroupDto} from 'src/app/services/groups/group-dto';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {MessageService} from 'src/app/messages/message.service';

@Component({
  selector: 'app-group-actions',
  templateUrl: './group-actions.component.html',
  styleUrls: ['./group-actions.component.scss']
})
export class GroupActionsComponent {
  @Input()
  group: GroupDto;

  constructor(
    private groupsService: GroupsService,
    private messageService: MessageService
  ) {
  }

  deleteGroup() {
    this.groupsService.getGroupUsage(this.group.id).then(usage => {
      if (usage.used) {
        this.messageService.showWarning(`Group ${this.group.id} still in use by: ${usage.users.join(', ')}`);
      } else {
        this.groupsService.deleteGroup(this.group.id)
          .then(() => this.messageService.showInfo(`Group ${this.group.id} deleted successfully`));
      }
    });
  }
}
