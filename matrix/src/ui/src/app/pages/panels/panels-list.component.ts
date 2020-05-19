import {Component, OnInit} from '@angular/core';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {PanelDto} from 'src/app/services/panels/panel-dto';
import {ActivatedRoute} from '@angular/router';
import {ternarySelection} from 'src/app/utils/ternary-selection';

@Component({
  selector: 'app-panels-list',
  templateUrl: './panels-list.component.html',
  styleUrls: ['./panels-list.component.scss']
})
export class PanelsListComponent implements OnInit {
  private allPanels: Array<PanelDto>;
  panels: Array<PanelDto>;

  filter: string;

  constructor(
    private panelsService: PanelsService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = paramMap.get('filter');
      this.applyFilter();
    });
  }

  private updateList() {
    this.panelsService.getConfiguredPanels().then(
      panels => {
        this.allPanels = panels;
        this.applyFilter();
      });
  }

  private applyFilter() {
    if (!this.allPanels) {
      this.panels = null;
      return;
    }

    const onlineFilter = ternarySelection(this.filter, 'online', 'offline');
    const assignedFilter = ternarySelection(this.filter, 'assigned', 'unassigned');
    this.panels = this.allPanels.filter(device => {
      if (onlineFilter != null && device.online !== onlineFilter) {
        return false;
      } else if (assignedFilter != null && device.assigned !== assignedFilter) {
        return false;
      }

      return true;
    });
  }

  assignDevice() {
  }

  addPanel() {
  }
}
