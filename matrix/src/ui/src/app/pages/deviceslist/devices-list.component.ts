import {Component, OnInit} from '@angular/core';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {DeviceDto} from 'src/app/services/devices/device-dto';

@Component({
  selector: 'app-devices-list',
  templateUrl: './devices-list.component.html',
  styleUrls: ['./devices-list.component.scss']
})
export class DevicesListComponent implements OnInit {
  devices: Array<DeviceDto>;

  constructor(private devicesService: DevicesService) {
  }

  ngOnInit(): void {
    this.updateList();
  }

  private updateList() {
    this.devicesService.getOnlineDevices().then(
      devices => this.devices = devices);
  }
}
