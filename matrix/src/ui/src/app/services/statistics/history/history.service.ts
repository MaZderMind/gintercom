import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {StatisticsDto} from 'src/app/services/statistics/statistics-dto';

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private httpClient: HttpClient) {
  }

  getMinutelyStatistics(): Promise<Array<StatisticsDto>> {
    return this.httpClient.get<StatisticsDto[]>('/rest/statistics/history/minutely').toPromise();
  }

  getQuarterHourlyStatistics(): Promise<Array<StatisticsDto>> {
    return this.httpClient.get<StatisticsDto[]>('/rest/statistics/history/quarterHourly').toPromise();
  }

  getHourlyStatistics(): Promise<Array<StatisticsDto>> {
    return this.httpClient.get<StatisticsDto[]>('/rest/statistics/history/hourly').toPromise();
  }
}
