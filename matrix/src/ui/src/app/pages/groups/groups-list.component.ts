import {Component, OnInit} from '@angular/core';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Component({
  selector: 'app-groups-list',
  templateUrl: './groups-list.component.html',
  styleUrls: ['./groups-list.component.scss']
})
export class GroupsListComponent implements OnInit {
  groups: Array<GroupDto>;

  constructor(private groupsService: GroupsService) {
  }

  ngOnInit(): void {
    this.updateList();
  }

  private updateList() {
    this.groupsService.getConfiguredGroups().then(
      groups => this.groups = groups);
  }

  addGroup() {
  }
}
