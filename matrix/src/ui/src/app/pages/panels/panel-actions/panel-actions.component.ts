import {Component, Input} from '@angular/core';
import {PanelDto} from 'src/app/services/panels/panel-dto';

@Component({
  selector: 'app-panel-actions',
  templateUrl: './panel-actions.component.html',
  styleUrls: ['./panel-actions.component.scss']
})
export class PanelActionsComponent {
  @Input()
  panel: PanelDto;

  deletePanel() {
  }

  assignDevice() {
  }

  unassignDevice() {
  }

}
