import {Component, Input} from '@angular/core';
import {ClientDto} from 'src/app/services/clients/client-dto';

@Component({
  selector: 'app-client-actions',
  templateUrl: './client-actions.component.html',
  styleUrls: ['./client-actions.component.scss']
})
export class ClientActionsComponent {

  @Input()
  client: ClientDto;

  forceDisconnect() {
  }

  unassignPanel() {
  }

  assignPanel() {
  }
}
