import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {ButtonsEditorComponent} from 'src/app/pages/panels/components/buttons-editor/buttons-editor.component';
import {ButtonSetsService} from 'src/app/services/button-sets/button-sets.service';
import {ButtonSetDto} from 'src/app/services/button-sets/button-set-dto';

@Component({
  selector: 'app-panel-edit',
  templateUrl: './panel-edit.component.html',
  styleUrls: ['./panel-edit.component.scss']
})
export class PanelEditComponent implements OnInit {
  panelEditForm = new FormGroup({
    id: new FormControl('', [Validators.required]),
    display: new FormControl(''),

    rxGroups: new FormControl([], Validators.required),
    txGroups: new FormControl([]),
    buttons: ButtonsEditorComponent.createControl(),
    buttonSets: new FormControl([
      'helpdesk',
    ]),
  });

  panelId: string;

  formValue: any;
  formStatus: any;

  constructor(
    private route: ActivatedRoute,
    private panelsService: PanelsService
  ) {
  }

  ngOnInit(): void {
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

    this.panelEditForm.valueChanges.subscribe(value => this.formValue = value);
    this.panelEditForm.statusChanges.subscribe(status => this.formStatus = status);
    this.formValue = this.panelEditForm.value;
    this.formStatus = this.panelEditForm.status;
  }

  isCreation(): boolean {
    return !this.panelId;
  }

  onSubmit() {
    console.log(this.panelEditForm.value);
  }
}
