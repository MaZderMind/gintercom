import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {StatusComponent} from './status.component';
import {StatisticsService} from 'src/app/services/statistics/statistics.service';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

describe('StatusComponent', () => {
  let component: StatusComponent;
  let fixture: ComponentFixture<StatusComponent>;
  let statisticsService: StatisticsService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    statisticsService = mock(StatisticsService);
    when(statisticsService.loadStatistics()).thenResolve({
      timestamp: '2020-01-01T10:00:00Z',

      groupsConfigured: 0,

      panelsConfigured: 0,
      panelsAssigned: 0,
      panelsUnassigned: 0,
      panelsOnline: 0,
      panelsOffline: 0,

      devicesOnline: 0,
      devicesProvisioned: 0,
      devicesUnprovisioned: 0,
    });

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [StatusComponent],
      providers: [
        {provide: StatisticsService, useFactory: () => instance(statisticsService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
