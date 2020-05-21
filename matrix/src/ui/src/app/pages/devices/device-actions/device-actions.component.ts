import {Component, Input} from '@angular/core';
import {DeviceDto} from 'src/app/services/devices/device-dto';

@Component({
  selector: 'app-device-actions',
  templateUrl: './device-actions.component.html',
  styleUrls: ['./device-actions.component.scss']
})
export class DeviceActionsComponent {

  @Input()
  device: DeviceDto;

  forceDisconnect() {
  }

  unassignPanel() {
  }

  assignPanel() {
  }
}
