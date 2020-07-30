import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {GroupsService} from 'src/app/services/groups/groups.service';

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent implements OnInit {
  groupEditForm = new FormGroup({
    groupId: new FormControl('', [Validators.required]),
    displayName: new FormControl(''),
  });

  groupId: string;

  constructor(
    private route: ActivatedRoute,
    private groupsService: GroupsService
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.groupId = params.get('id');
    });
  }

  onSubmit() {
    // this.groupsService.addGroup()
    this.groupEditForm.disable();
  }
}
