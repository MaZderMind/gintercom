import {Component, OnInit} from '@angular/core';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {DeviceDto} from 'src/app/services/devices/device-dto';
import {ActivatedRoute} from '@angular/router';
import {Filter, Filters} from 'src/app/utils/filter-util';

@Component({
  selector: 'app-devices-list',
  templateUrl: './devices-list.component.html',
  styleUrls: ['./devices-list.component.scss']
})
export class DevicesListComponent implements OnInit {
  private static readonly filters: Filters<DeviceDto> = new Filters(
    'Connected Devices',
    new Filter('provisioned', 'Provisioned Devices', device => device.provisioned),
    new Filter('unprovisioned', 'Unprovisioned Devices', device => !device.provisioned),
  );

  devices: Array<DeviceDto>;
  filter: Filter<DeviceDto>;
  private allDevices: Array<DeviceDto>;

  constructor(
    private devicesService: DevicesService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = DevicesListComponent.filters.select(paramMap.get('filter'));
      this.devices = this.filter.apply(this.allDevices);
    });
  }

  assignPanel() {
  }

  private updateList() {
    this.devicesService.getOnlineDevices().then(devices => {
      this.allDevices = devices;
      this.devices = this.filter.apply(this.allDevices);
    });
  }
}
