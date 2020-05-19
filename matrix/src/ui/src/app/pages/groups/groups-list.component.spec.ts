import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';

describe('GroupsComponent', () => {
  let component: GroupsListComponent;
  let fixture: ComponentFixture<GroupsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GroupsListComponent]
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
