import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelEditComponent} from './panel-edit.component';
import {ActivatedRoute} from '@angular/router';
import {EMPTY, Subscription} from 'rxjs';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {MockComponent} from 'ng-mocks';
import {GroupMultiSelectComponent} from 'src/app/components/group-multi-select/group-multi-select.component';
import {ButtonsEditorComponent} from 'src/app/pages/panels/components/buttons-editor/buttons-editor.component';

describe('PanelEditComponent', () => {
  let component: PanelEditComponent;
  let fixture: ComponentFixture<PanelEditComponent>;

  let panelService: PanelsService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    panelService = mock(PanelsService);
    when(panelService.getConfiguredPanels()).thenResolve([]);

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [
        PanelEditComponent,
        MockComponent(GroupMultiSelectComponent),
        MockComponent(ButtonsEditorComponent),
      ],
      providers: [
        {provide: PanelsService, useFactory: () => instance(panelService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: EMPTY,
          }
        },
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
