import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupActionsComponent} from './group-actions.component';
import {RouterTestingModule} from '@angular/router/testing';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {instance, mock} from 'ts-mockito';

describe('GroupActionsComponent', () => {
  let component: GroupActionsComponent;
  let fixture: ComponentFixture<GroupActionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GroupActionsComponent],
      imports: [RouterTestingModule],
      providers: [
        {provide: GroupsService, useFactory: () => instance(mock(GroupsService))},
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupActionsComponent);
    component = fixture.componentInstance;
    component.group = {
      id: 'foo',
      display: 'Foo',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
