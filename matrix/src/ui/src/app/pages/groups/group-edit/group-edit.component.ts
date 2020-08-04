import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {MessageService} from 'src/app/messages/message.service';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent implements OnInit {
  groupEditForm = new FormGroup({
    id: new FormControl('', [Validators.required]),
    display: new FormControl(''),
  });

  @Input()
  groupId: string;

  @Output()
  saved = new EventEmitter<void>();

  constructor(
    private groupsService: GroupsService,
    private messageService: MessageService
  ) {
  }

  isCreation(): boolean {
    return !this.groupId;
  }

  ngOnInit(): void {
    if (this.groupId) {
      this.groupsService.getGroup(this.groupId).then(
        group => {
          this.groupEditForm.setValue(group);
          this.groupEditForm.get('id').disable();
        });
    }
  }

  onSubmit() {
    const groupDto: GroupDto = this.groupEditForm.value;

    this.groupEditForm.disable();
    if (this.isCreation()) {
      this.groupsService.addGroup(groupDto)
        .then(() => {
          this.messageService.showInfo(`Group ${groupDto.id} created successfully`);
          this.saved.emit();
        })
        .finally(() => this.groupEditForm.enable());
    } else {
      groupDto.id = this.groupId;
      this.groupsService.updateGroup(groupDto)
        .then(() => {
          this.messageService.showInfo(`Group ${groupDto.id} created saved`);
          this.saved.emit();
        })
        .finally(() => this.groupEditForm.enable());
    }
  }
}
