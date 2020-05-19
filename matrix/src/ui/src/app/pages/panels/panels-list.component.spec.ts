import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {instance, mock, when} from 'ts-mockito';
import {PanelsService} from 'src/app/services/panels/panels.service';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute} from '@angular/router';
import {EMPTY} from 'rxjs';

describe('PanelsListComponent', () => {
  let component: PanelsListComponent;
  let fixture: ComponentFixture<PanelsListComponent>;
  let panelsService: PanelsService;

  beforeEach(async(() => {
    panelsService = mock(PanelsService);
    when(panelsService.getConfiguredPanels()).thenResolve([]);
    TestBed.configureTestingModule({
      declarations: [PanelsListComponent],
      providers: [
        {provide: PanelsService, useFactory: () => instance(panelsService)},
        {
          provide: ActivatedRoute, useValue: {
            paramMap: EMPTY,
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
