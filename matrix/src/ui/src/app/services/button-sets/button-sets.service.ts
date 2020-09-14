import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ButtonSetDto} from 'src/app/services/button-sets/button-set-dto';

@Injectable({
  providedIn: 'root'
})
export class ButtonSetsService {

  constructor(private httpClient: HttpClient) {
  }

  getConfiguredButtonSets(): Promise<Array<ButtonSetDto>> {
    return this.httpClient.get<Array<ButtonSetDto>>('/rest/buttonSets').toPromise();
  }
}
