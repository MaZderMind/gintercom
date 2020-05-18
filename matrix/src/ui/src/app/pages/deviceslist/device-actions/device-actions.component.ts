import {Component, Input, OnInit} from '@angular/core';
import {DeviceDto} from 'src/app/services/devices/device-dto';

@Component({
  selector: 'app-device-actions',
  templateUrl: './device-actions.component.html',
  styleUrls: ['./device-actions.component.scss']
})
export class DeviceActionsComponent implements OnInit {

  @Input()
  device: DeviceDto;

}
