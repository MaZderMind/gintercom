import {Component, OnInit} from '@angular/core';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {DeviceDto} from 'src/app/services/devices/device-dto';
import {ActivatedRoute} from '@angular/router';
import {ternarySelection} from 'src/app/utils/ternary-selection';

@Component({
  selector: 'app-devices-list',
  templateUrl: './devices-list.component.html',
  styleUrls: ['./devices-list.component.scss']
})
export class DevicesListComponent implements OnInit {
  private allDevices: Array<DeviceDto>;
  devices: Array<DeviceDto>;

  filter: string;

  constructor(
    private devicesService: DevicesService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = paramMap.get('filter');
      this.applyFilter();
    });
  }

  private updateList() {
    this.devicesService.getOnlineDevices().then(devices => {
      this.allDevices = devices;
      this.applyFilter();
    });
  }

  private applyFilter() {
    if (!this.allDevices) {
      this.devices = null;
      return;
    }

    const provisionedFilter = ternarySelection(this.filter, 'provisioned', 'unprovisioned');
    this.devices = this.allDevices.filter(device => {
      if (provisionedFilter != null && device.provisioned !== provisionedFilter) {
        return false;
      }

      return true;
    });
  }

  assignPanel() {
  }

}
