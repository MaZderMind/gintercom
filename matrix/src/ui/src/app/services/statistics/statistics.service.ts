import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {StatisticsDto} from 'src/app/services/statistics/statistics-dto';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  constructor(private httpClient: HttpClient) {
  }

  loadStatistics(): Promise<StatisticsDto> {
    return this.httpClient.get<StatisticsDto>('/rest/statistics').toPromise();
  }
}
