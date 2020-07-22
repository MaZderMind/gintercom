import {Component, Input} from '@angular/core';
import {ClientDto} from 'src/app/services/clients/client-dto';

@Component({
  selector: 'app-client-status',
  templateUrl: './client-status.component.html',
  styleUrls: ['./client-status.component.scss']
})
export class ClientStatusComponent {
  @Input()
  client: ClientDto;
}
