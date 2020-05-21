import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute} from '@angular/router';
import {of, Subscription} from 'rxjs';
import {UiUpdateService} from 'src/app/services/ui-update.service';

describe('PanelsListComponent', () => {
  let component: PanelsListComponent;
  let fixture: ComponentFixture<PanelsListComponent>;
  let panelsService: PanelsService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    panelsService = mock(PanelsService);
    when(panelsService.getConfiguredPanels()).thenResolve([]);

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [PanelsListComponent],
      providers: [
        {provide: PanelsService, useFactory: () => instance(panelsService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: of({get: () => undefined}),
          }
        },
      ],
      imports: [RouterTestingModule],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
