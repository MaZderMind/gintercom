import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';
import {ActivatedRoute} from '@angular/router';
import {Filter, Filters} from 'src/app/utils/filter-util';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-panels-list',
  templateUrl: './panels-list.component.html',
  styleUrls: ['./panels-list.component.scss']
})
export class PanelsListComponent implements OnInit, OnDestroy {
  private static readonly filters: Filters<PanelInfoDto> = new Filters(
    'Configured Panels',
    new Filter('online', 'Online Panels', panel => panel.online),
    new Filter('offline', 'Offline Panels', panel => !panel.online),
    new Filter('assigned', 'Assigned Panels', panel => panel.assigned),
    new Filter('unassigned', 'Unassigned Panels', panel => !panel.assigned),
  );

  panels: Array<PanelInfoDto>;
  filter: Filter<PanelInfoDto>;
  private allPanels: Array<PanelInfoDto>;
  private uiUpdateSubscription: Subscription;

  constructor(
    private panelsService: PanelsService,
    private activatedRoute: ActivatedRoute,
    private uiUpdateService: UiUpdateService
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.uiUpdateSubscription = this.uiUpdateService.subscribe(() => this.updateList());
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = PanelsListComponent.filters.select(paramMap.get('filter'));
      this.panels = this.filter.apply(this.allPanels);
    });
  }

  ngOnDestroy() {
    this.uiUpdateSubscription.unsubscribe();
  }

  assignClient() {
  }

  addPanel() {
  }

  private updateList() {
    this.panelsService.getConfiguredPanels().then(
      panels => {
        this.allPanels = panels;
        this.panels = this.filter.apply(this.allPanels);
      });
  }
}
