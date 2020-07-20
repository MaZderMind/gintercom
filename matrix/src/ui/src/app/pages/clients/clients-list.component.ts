import {Component, OnDestroy, OnInit} from '@angular/core';
import {ClientsService} from 'src/app/services/clients/clients.service';
import {ClientDto} from 'src/app/services/clients/client-dto';
import {ActivatedRoute} from '@angular/router';
import {Filter, Filters} from 'src/app/utils/filter-util';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-clients-list',
  templateUrl: './clients-list.component.html',
  styleUrls: ['./clients-list.component.scss']
})
export class ClientsListComponent implements OnInit, OnDestroy {
  private static readonly filters: Filters<ClientDto> = new Filters(
    'Connected Clients',
    new Filter('provisioned', 'Provisioned Clients', client => client.provisioned),
    new Filter('unprovisioned', 'Unprovisioned Clients', client => !client.provisioned),
  );

  clients: Array<ClientDto>;
  filter: Filter<ClientDto>;
  private allClients: Array<ClientDto>;

  private uiUpdateSubscription: Subscription;

  constructor(
    private clientsService: ClientsService,
    private activatedRoute: ActivatedRoute,
    private uiUpdateService: UiUpdateService
  ) {
  }

  ngOnInit(): void {
    this.updateList();
    this.uiUpdateSubscription = this.uiUpdateService.subscribe(() => this.updateList());
    this.activatedRoute.paramMap.subscribe(paramMap => {
      this.filter = ClientsListComponent.filters.select(paramMap.get('filter'));
      this.clients = this.filter.apply(this.allClients);
    });
  }

  ngOnDestroy() {
    this.uiUpdateSubscription.unsubscribe();
  }

  assignPanel() {
  }

  private updateList() {
    this.clientsService.getOnlineClients().then(clients => {
      this.allClients = clients;
      this.clients = this.filter.apply(this.allClients);
    });
  }
}
