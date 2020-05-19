import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {instance, mock, when} from 'ts-mockito';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {RouterTestingModule} from '@angular/router/testing';

describe('GroupsListComponent', () => {
  let component: GroupsListComponent;
  let fixture: ComponentFixture<GroupsListComponent>;
  let groupsService: GroupsService;

  beforeEach(async(() => {
    groupsService = mock(GroupsService);
    when(groupsService.getConfiguredGroups()).thenResolve([]);
    TestBed.configureTestingModule({
      declarations: [GroupsListComponent],
      providers: [
        {provide: GroupsService, useFactory: () => instance(groupsService)},
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
