import {Component, OnDestroy, OnInit} from '@angular/core';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {GroupDto} from 'src/app/services/groups/group-dto';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-groups-list',
  templateUrl: './groups-list.component.html',
  styleUrls: ['./groups-list.component.scss']
})
export class GroupsListComponent implements OnInit, OnDestroy {
  groups: Array<GroupDto>;
  private uiUpdateSubscription: Subscription;

  constructor(
    private groupsService: GroupsService,
    private uiUpdateService: UiUpdateService
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.uiUpdateSubscription = this.uiUpdateService.subscribe(() => this.updateList());
  }

  ngOnDestroy() {
    this.uiUpdateSubscription.unsubscribe();
  }

  private updateList() {
    this.groupsService.getConfiguredGroups().then(
      groups => this.groups = groups);
  }
}
