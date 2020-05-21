import {Component, OnDestroy, OnInit} from '@angular/core';
import {StatisticsDto} from 'src/app/services/statistics/statistics-dto';
import {StatisticsService} from 'src/app/services/statistics/statistics.service';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss']
})
export class StatusComponent implements OnInit, OnDestroy {
  statistics: StatisticsDto;
  private uiUpdateSubscription: Subscription;

  constructor(
    private statisticsService: StatisticsService,
    private uiUpdateService: UiUpdateService
  ) {
  }

  ngOnInit(): void {
    this.updateStatistics();
    this.uiUpdateSubscription = this.uiUpdateService.subscribe(() => this.updateStatistics());
  }

  ngOnDestroy(): void {
    this.uiUpdateSubscription.unsubscribe();
  }

  private updateStatistics() {
    this.statisticsService.loadStatistics()
      .then(statistics => this.statistics = statistics);
  }
}
