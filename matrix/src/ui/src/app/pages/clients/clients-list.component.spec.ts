import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClientsListComponent} from 'src/app/pages/clients/clients-list.component';
import {ClientsService} from 'src/app/services/clients/clients.service';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {ActivatedRoute} from '@angular/router';
import {of, Subscription} from 'rxjs';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {CommonModule} from '@angular/common';

describe('ClientsListComponent', () => {
  let component: ClientsListComponent;
  let fixture: ComponentFixture<ClientsListComponent>;
  let clientsService: ClientsService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    clientsService = mock(ClientsService);
    when(clientsService.getOnlineClients()).thenResolve([]);

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [ClientsListComponent],
      providers: [
        {provide: ClientsService, useFactory: () => instance(clientsService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of({get: () => undefined}),
          }
        },
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
