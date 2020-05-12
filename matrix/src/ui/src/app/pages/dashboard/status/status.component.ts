import {Component, OnInit} from '@angular/core';
import {StatisticsDto} from 'src/app/services/statistics/statistics-dto';
import {StatisticsService} from 'src/app/services/statistics/statistics.service';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss']
})
export class StatusComponent implements OnInit {
  statistics: StatisticsDto;

  constructor(private statisticsService: StatisticsService) {
  }

  ngOnInit(): void {
    this.statisticsService.loadStatistics()
      .then(statistics => this.statistics = statistics);
  }
}
