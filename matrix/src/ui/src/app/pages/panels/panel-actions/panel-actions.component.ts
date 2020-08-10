import {Component, Input} from '@angular/core';
import {PanelInfoDto} from 'src/app/services/panels/panel-info-dto';

@Component({
  selector: 'app-panel-actions',
  templateUrl: './panel-actions.component.html',
  styleUrls: ['./panel-actions.component.scss']
})
export class PanelActionsComponent {
  @Input()
  panel: PanelInfoDto;

  deletePanel() {
  }

  assignClient() {
  }

  unassignClient() {
  }

}
