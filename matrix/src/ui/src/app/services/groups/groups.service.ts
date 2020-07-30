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

  getGroup(groupId: string): Promise<GroupDto> {
    return this.httpClient.get<GroupDto>(`/rest/group/${groupId}`).toPromise();
  }

  addGroup(groupDto: GroupDto): Promise<void> {
    return this.httpClient.post<void>('/rest/group', groupDto).toPromise();
  }

  deleteGroup(groupId: string): Promise<void> {
    return this.httpClient.delete<void>(`/rest/group/${groupId}`).toPromise();
  }
}
