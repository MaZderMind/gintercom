import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {PanelsService} from 'src/app/services/panels/panels.service';

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
    txGroups: new FormControl([]),
    buttons: new FormControl([])
  });

  panelId: string;

  foo: any;

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

    this.panelEditForm.valueChanges.subscribe(value => this.foo = value);
    this.foo = this.panelEditForm.value;
  }

  isCreation(): boolean {
    return !this.panelId;
  }

  onSubmit() {
    console.log(this.panelEditForm.value);
  }
}
