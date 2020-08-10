import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';

@Injectable({
  providedIn: 'root'
})
export class PanelsService {
  constructor(private httpClient: HttpClient) {
  }

  getConfiguredPanels(): Promise<Array<PanelInfoDto>> {
    return this.httpClient.get<Array<PanelInfoDto>>('/rest/panels').toPromise();
  }

  getPanel(panelId: string): Promise<PanelInfoDto> {
    return this.httpClient.get<PanelInfoDto>(`/rest/panel/${panelId}`).toPromise();
  }
}
