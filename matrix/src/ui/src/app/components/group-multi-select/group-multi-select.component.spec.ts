import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupMultiSelectComponent} from './group-multi-select.component';
import {instance, mock, when} from 'ts-mockito';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {MockComponent} from 'ng-mocks';
import {CheckboxMultiSelectComponent} from 'src/app/components/checkbox-multi-select/checkbox-multi-select.component';

describe('GroupMultiSelectComponent', () => {
  let component: GroupMultiSelectComponent;
  let fixture: ComponentFixture<GroupMultiSelectComponent>;
  let groupsService: GroupsService;

  beforeEach(async(() => {
    groupsService = mock(GroupsService);
    when(groupsService.getConfiguredGroups()).thenResolve([]);

    TestBed.configureTestingModule({
      declarations: [
        GroupMultiSelectComponent,
        MockComponent(CheckboxMultiSelectComponent),
      ],
      providers: [
        {provide: GroupsService, useFactory: () => instance(groupsService)},
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
