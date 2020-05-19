import {Component, Input} from '@angular/core';
import {PanelDto} from 'src/app/services/panels/panel-dto';

@Component({
  selector: 'app-panel-status',
  templateUrl: './panel-status.component.html',
  styleUrls: ['./panel-status.component.scss']
})
export class PanelStatusComponent {
  @Input()
  panel: PanelDto;
}
