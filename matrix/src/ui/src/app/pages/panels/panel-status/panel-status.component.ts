import {Component, Input} from '@angular/core';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';

@Component({
  selector: 'app-panel-status',
  templateUrl: './panel-status.component.html',
  styleUrls: ['./panel-status.component.scss']
})
export class PanelStatusComponent {
  @Input()
  panel: PanelInfoDto;
}
