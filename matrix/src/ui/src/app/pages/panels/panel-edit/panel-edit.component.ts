import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-panel-edit',
  templateUrl: './panel-edit.component.html',
  styleUrls: ['./panel-edit.component.scss']
})
export class PanelEditComponent implements OnInit {
  selectedId: string;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.selectedId = params.get('id');
    });
  }

}
