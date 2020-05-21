import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {anyFunction, instance, mock, when} from 'ts-mockito';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {RouterTestingModule} from '@angular/router/testing';
import {UiUpdateService} from 'src/app/services/ui-update.service';
import {Subscription} from 'rxjs';

describe('GroupsListComponent', () => {
  let component: GroupsListComponent;
  let fixture: ComponentFixture<GroupsListComponent>;
  let groupsService: GroupsService;
  let uiUpdateService: UiUpdateService;

  beforeEach(async(() => {
    groupsService = mock(GroupsService);
    when(groupsService.getConfiguredGroups()).thenResolve([]);

    uiUpdateService = mock(UiUpdateService);
    when(uiUpdateService.subscribe(anyFunction())).thenReturn(new Subscription());

    TestBed.configureTestingModule({
      declarations: [GroupsListComponent],
      providers: [
        {provide: GroupsService, useFactory: () => instance(groupsService)},
        {provide: UiUpdateService, useFactory: () => instance(uiUpdateService)},
      ],
      imports: [RouterTestingModule],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
