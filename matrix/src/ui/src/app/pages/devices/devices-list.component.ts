import {Component, OnDestroy, OnInit} from '@angular/core';
import {DevicesService} from 'src/app/services/devices/devices.service';
import {DeviceDto} from 'src/app/services/devices/device-dto';
import {ActivatedRoute} from '@angular/router';
import {Filter, Filters} from 'src/app/utils/filter-util';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-devices-list',
  templateUrl: './devices-list.component.html',
  styleUrls: ['./devices-list.component.scss']
})
export class DevicesListComponent implements OnInit, OnDestroy {
  private static readonly filters: Filters<DeviceDto> = new Filters(
    'Connected Devices',
    new Filter('provisioned', 'Provisioned Devices', device => device.provisioned),
    new Filter('unprovisioned', 'Unprovisioned Devices', device => !device.provisioned),
  );

  devices: Array<DeviceDto>;
  filter: Filter<DeviceDto>;
  private allDevices: Array<DeviceDto>;

  private uiUpdateSubscription: Subscription;

  constructor(
    private devicesService: DevicesService,
    private activatedRoute: ActivatedRoute,
    private uiUpdateService: UiUpdateService
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.uiUpdateSubscription = this.uiUpdateService.subscribe(() => this.updateList());
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = DevicesListComponent.filters.select(paramMap.get('filter'));
      this.devices = this.filter.apply(this.allDevices);
    });
  }

  ngOnDestroy() {
    this.uiUpdateSubscription.unsubscribe();
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
