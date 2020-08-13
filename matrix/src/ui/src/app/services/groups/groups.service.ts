import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GroupDto} from 'src/app/services/groups/group-dto';
import {UsageDto} from 'src/app/services/usage-dto';
import {Cache} from 'src/app/utils/cache-decorator';

@Injectable({
  providedIn: 'root'
})
export class GroupsService {
  constructor(private httpClient: HttpClient) {
  }

  @Cache()
  getConfiguredGroups(): Promise<Array<GroupDto>> {
    return this.httpClient.get<Array<GroupDto>>('/rest/groups').toPromise();
  }

  @Cache()
  getGroup(groupId: string): Promise<GroupDto> {
    return this.httpClient.get<GroupDto>(`/rest/groups/${groupId}`).toPromise();
  }

  addGroup(groupDto: GroupDto): Promise<void> {
    return this.httpClient.post<void>('/rest/groups', groupDto).toPromise();
  }

  updateGroup(groupDto: GroupDto) {
    return this.httpClient.put<void>('/rest/groups', groupDto).toPromise();
  }

  deleteGroup(groupId: string): Promise<void> {
    return this.httpClient.delete<void>(`/rest/groups/${groupId}`).toPromise();
  }

  @Cache()
  getGroupUsage(groupId: string): Promise<UsageDto> {
    return this.httpClient.get<UsageDto>(`/rest/groups/${groupId}/usage`).toPromise();
  }
}
