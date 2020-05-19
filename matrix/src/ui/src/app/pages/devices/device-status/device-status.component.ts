import {Component, Input} from '@angular/core';
import {DeviceDto} from 'src/app/services/devices/device-dto';

@Component({
  selector: 'app-device-status',
  templateUrl: './device-status.component.html',
  styleUrls: ['./device-status.component.scss']
})
export class DeviceStatusComponent {
  @Input()
  device: DeviceDto;
}
