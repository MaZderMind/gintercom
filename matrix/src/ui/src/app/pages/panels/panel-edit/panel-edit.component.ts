import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Component({
  selector: 'app-panel-edit',
  templateUrl: './panel-edit.component.html',
  styleUrls: ['./panel-edit.component.scss']
})
export class PanelEditComponent implements OnInit {
  panelEditForm = new FormGroup({
    id: new FormControl('', [Validators.required]),
    display: new FormControl(''),

    rxGroups: new FormControl([]),
    txGroups: new FormControl([])
  });

  panelId: string;

  groups: Array<GroupDto>;

  foo: any;

  constructor(
    private route: ActivatedRoute,
    private panelsService: PanelsService,
    private groupsService: GroupsService
  ) {
  }

  ngOnInit(): void {
    this.groupsService.getConfiguredGroups().then(groups => {
      this.groups = groups;

      this.panelEditForm.setValue({
        id: 'bar',
        display: 'foo',
        rxGroups: ['cams-a'],
        txGroups: []
      });
    });

    this.route.paramMap.subscribe(params => {
      this.panelId = params.get('id');

      if (this.panelId) {
        this.panelsService.getPanel(this.panelId).then(
          panel => {
            this.panelEditForm.setValue(panel);
            this.panelEditForm.get('id').disable();
          });
      }
    });

    this.panelEditForm.valueChanges.subscribe(value => this.foo = value);
  }

  isCreation(): boolean {
    return !this.panelId;
  }

  onSubmit() {
    console.log(this.panelEditForm.value);
  }

  groupValueGetter(group: GroupDto) {
    return group.id;
  }
}
