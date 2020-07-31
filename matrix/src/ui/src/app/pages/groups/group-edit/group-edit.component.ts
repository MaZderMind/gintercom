import {Component, EventEmitter, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {MessageService} from 'src/app/messages/message.service';
import {GroupDto} from 'src/app/services/groups/group-dto';
import {HttpErrorResponse} from '@angular/common/http';

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

  groupId: string;

  success = new EventEmitter<void>()

  constructor(
    private route: ActivatedRoute,
    private groupsService: GroupsService,
    private messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.groupId = params.get('id');
    });
  }

  onSubmit() {
    const groupDto: GroupDto = this.groupEditForm.value;

    this.groupEditForm.disable();
    this.groupsService.addGroup(groupDto)
      .then(() => {
        this.messageService.showInfo(`Group ${groupDto.id} created successfully`);
        this.success.emit();
      })
      .finally(() => this.groupEditForm.enable());
  }
}
