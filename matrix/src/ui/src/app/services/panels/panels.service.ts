import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';
import {Cache} from 'src/app/utils/cache-decorator';

@Injectable({
  providedIn: 'root'
})
export class PanelsService {
  constructor(private httpClient: HttpClient) {
  }

  @Cache()
  getConfiguredPanels(): Promise<Array<PanelInfoDto>> {
    return this.httpClient.get<Array<PanelInfoDto>>('/rest/panels').toPromise();
  }

  @Cache()
  getPanel(panelId: string): Promise<PanelInfoDto> {
    return this.httpClient.get<PanelInfoDto>(`/rest/panel/${panelId}`).toPromise();
  }
}
