import {Component, OnInit} from '@angular/core';
import {HistoryService} from 'src/app/services/statistics/history.service';
import {StatisticsDto} from 'src/app/services/statistics/statistics-dto';

type LineChartDataPoint = { name: string | Date, value: number };
type LineChartData = Array<{ name: string, series: Array<LineChartDataPoint> }>;

interface TimeWindow {
  id: string;
  name: string;
  dateFormat: string;
  updateIntervalMinutes: number;
}

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  readonly timeWindows: TimeWindow[] = [{
    id: 'minutely',
    name: 'Short-Term (90 Minutes, Minutely)',
    dateFormat: 'hh:mm',
    updateIntervalMinutes: 1
  }, {
    id: 'quarterHourly',
    name: 'Mid-Term (24 Hours, Quarter Hourly)',
    dateFormat: 'hh:mm',
    updateIntervalMinutes: 5
  }, {
    id: 'hourly',
    name: 'Long-Term (4 Days, Hourly)',
    dateFormat: 'EEEEEE hh:mm',
    updateIntervalMinutes: 5
  }]

  timeWindow = this.timeWindows[0];
  chartData: LineChartData;
  updateInterval: ReturnType<typeof setInterval>;

  constructor(private historyService: HistoryService) {
  }

  ngOnInit() {
    this.updateData();
    this.scheduleUpdate();
  }

  timeWindowChanged() {
    this.updateData();
    this.scheduleUpdate();
  }

  private updateData() {
    this.getDataForSelectedTimeWindow()
      .then(timeline => this.chartData = HistoryComponent.formatChartData(timeline))
  }

  private scheduleUpdate() {
    clearInterval(this.updateInterval);
    this.updateInterval = setInterval(
      () => this.updateData(),
      this.timeWindow.updateIntervalMinutes * 60 * 1000
    )
  }

  private getDataForSelectedTimeWindow(): Promise<Array<StatisticsDto>> {
    switch (this.timeWindow.id) {
      case 'minutely':
        return this.historyService.getMinutelyStatistics();

      case 'quarterHourly':
        return this.historyService.getQuarterHourlyStatistics();

      case 'hourly':
        return this.historyService.getHourlyStatistics();

      default:
        return Promise.reject('Unknown Time-Window: ' + this.timeWindow.id)
    }
  }

  private static formatChartData(timeline: Array<StatisticsDto>): LineChartData {
    return [
      {
        name: 'Panels (Configured)',
        series: HistoryComponent.formatChartSeries(timeline, 'panelsConfigured')
      },
      {
        name: 'Panels (Assigned)',
        series: HistoryComponent.formatChartSeries(timeline, 'panelsAssigned')
      },
      {
        name: 'Panels (Online)',
        series: HistoryComponent.formatChartSeries(timeline, 'panelsOnline')
      }
    ]
  }

  private static formatChartSeries(timeline: Array<StatisticsDto>, metric: string): Array<LineChartDataPoint> {
    return timeline.map(dataPoint => ({
      name: new Date(dataPoint.timestamp),
      value: dataPoint[metric]
    }));
  }
}
