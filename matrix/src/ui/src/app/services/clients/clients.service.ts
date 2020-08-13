import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ClientDto} from 'src/app/services/clients/client-dto';
import {Cache} from 'src/app/utils/cache-decorator';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {
  constructor(private httpClient: HttpClient) {
  }

  @Cache()
  getOnlineClients(): Promise<Array<ClientDto>> {
    return this.httpClient.get<Array<ClientDto>>('/rest/clients').toPromise();
  }
}
