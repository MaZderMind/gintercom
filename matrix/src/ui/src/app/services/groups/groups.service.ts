import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GroupDto} from 'src/app/services/groups/group-dto';

@Injectable({
  providedIn: 'root'
})
export class GroupsService {
  constructor(private httpClient: HttpClient) {
  }

  getConfiguredGroups(): Promise<Array<GroupDto>> {
    return this.httpClient.get<Array<GroupDto>>('/rest/groups').toPromise();
  }
}
