import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {instance, mock, when} from 'ts-mockito';
import {HistoryComponent} from './history.component';
import {HistoryService} from 'src/app/services/statistics/history/history.service';
import {BrowserTestingModule} from '@angular/platform-browser/testing';
import {FormsModule} from '@angular/forms';

describe('HistoryComponent', () => {
  let component: HistoryComponent;
  let fixture: ComponentFixture<HistoryComponent>;
  let historyServiceMock: HistoryService;

  beforeEach(async(() => {
    historyServiceMock = mock(HistoryService);
    when(historyServiceMock.getMinutelyStatistics()).thenResolve([]);
    when(historyServiceMock.getHourlyStatistics()).thenResolve([]);
    when(historyServiceMock.getQuarterHourlyStatistics()).thenResolve([]);

    TestBed.configureTestingModule({
      declarations: [HistoryComponent],
      providers: [
        {provide: HistoryService, useFactory: () => instance(historyServiceMock)},
      ],
      imports: [BrowserTestingModule, FormsModule],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
