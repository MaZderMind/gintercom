import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {DashboardComponent} from './dashboard.component';
import {MockComponent} from 'ng-mocks';
import {StatusComponent} from 'src/app/pages/dashboard/status/status.component';
import {HistoryComponent} from 'src/app/pages/dashboard/history/history.component';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {instance, mock} from 'ts-mockito';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        DashboardComponent,
        MockComponent(StatusComponent),
        MockComponent(HistoryComponent),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
