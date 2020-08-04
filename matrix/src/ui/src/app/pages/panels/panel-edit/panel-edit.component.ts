import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-panel-edit',
  templateUrl: './panel-edit.component.html',
  styleUrls: ['./panel-edit.component.scss']
})
export class PanelEditComponent implements OnInit {
  panelEditForm = new FormGroup({
    id: new FormControl('', [Validators.required]),
    display: new FormControl(''),
  });

  panelId: string;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.panelId = params.get('id');
    });
  }

  isCreation(): boolean {
    return !this.panelId;
  }

  onSubmit() {
  }
}
