import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ClientDto} from 'src/app/services/clients/client-dto';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {
  constructor(private httpClient: HttpClient) {
  }

  getOnlineClients(): Promise<Array<ClientDto>> {
    return this.httpClient.get<Array<ClientDto>>('/rest/clients').toPromise();
  }
}
