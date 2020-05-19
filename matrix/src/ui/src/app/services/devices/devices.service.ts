import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DeviceDto} from 'src/app/services/devices/device-dto';

@Injectable({
  providedIn: 'root'
})
export class DevicesService {
  constructor(private httpClient: HttpClient) {
  }

  getOnlineDevices(): Promise<Array<DeviceDto>> {
    return this.httpClient.get<Array<DeviceDto>>('/rest/devices').toPromise();
  }
}
