import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PanelDto} from 'src/app/services/panels/panel-dto';

@Injectable({
  providedIn: 'root'
})
export class PanelsService {
  constructor(private httpClient: HttpClient) {
  }

  getConfiguredPanels(): Promise<Array<PanelDto>> {
    return this.httpClient.get<Array<PanelDto>>('/rest/panels').toPromise();
  }
}
