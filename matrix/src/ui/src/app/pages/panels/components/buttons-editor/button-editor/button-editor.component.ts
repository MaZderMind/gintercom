import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Component({
  selector: 'app-button-editor',
  templateUrl: './button-editor.component.html',
  styleUrls: ['./button-editor.component.scss'],
})
export class ButtonEditorComponent implements OnInit {
  @Input()
  control: AbstractControl;

  @Input()
  showLabels = false;

  @Input()
  showDelete = false;

  @Output()
  delete = new EventEmitter<void>();

  targets: PanelInfoDto[] | GroupDto[];

  constructor(
    private groupsService: GroupsService,
    private panelsService: PanelsService
  ) {
  }

  get buttonForm(): FormGroup {
    return this.control as FormGroup;
  }

  static createControl(index): AbstractControl {
    return new FormGroup({
      id: new FormControl(String(index), Validators.required),
      display: new FormControl(''),
      targetType: new FormControl('GROUP', Validators.required),
      target: new FormControl('', Validators.required),
    });
  }

  ngOnInit(): void {
    this.loadTargets();

    this.buttonForm.get('targetType').valueChanges
      .subscribe(() => this.loadTargets());
  }

  private loadTargets() {
    const targetType = this.buttonForm.get('targetType').value;
    switch (targetType) {
      case 'GROUP':
        this.groupsService.getConfiguredGroups().then(
          groups => this.targets = groups);
        break;
      case 'PANEL':
        this.panelsService.getConfiguredPanels().then(
          panels => this.targets = panels);
        break;
    }

    this.buttonForm.get('target').setValue('');
  }

  getDisplayPlaceholder(): string {
    const selectedTarget = this.buttonForm.get('target').value;
    if (selectedTarget) {
      const targetObject = this.targets.find(target => target.id === selectedTarget);

      if (targetObject) {
        return targetObject.display;
      }
    }

    return '(Optional)';
  }
}
