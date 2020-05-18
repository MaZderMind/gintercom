import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {instance, mock, when} from 'ts-mockito';
import {StatusComponent} from './status.component';
import {StatisticsService} from 'src/app/services/statistics/statistics.service';

describe('StatusComponent', () => {
  let component: StatusComponent;
  let fixture: ComponentFixture<StatusComponent>;
  let statisticsService: StatisticsService;

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

    TestBed.configureTestingModule({
      declarations: [StatusComponent],
      providers: [
        {provide: StatisticsService, useFactory: () => instance(statisticsService)},
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
