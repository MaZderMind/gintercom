import {Component, OnInit} from '@angular/core';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {PanelDto} from 'src/app/services/panels/panel-dto';

@Component({
  selector: 'app-panels-list',
  templateUrl: './panels-list.component.html',
  styleUrls: ['./panels-list.component.scss']
})
export class PanelsListComponent implements OnInit {
  panels: Array<PanelDto>;

  constructor(private panelsService: PanelsService) {
  }

  ngOnInit(): void {
    this.updateList();
  }

  private updateList() {
    this.panelsService.getConfiguredPanels().then(
      panels => this.panels = panels);
  }

  assignDevice() {
  }

  addPanel() {
  }
}
