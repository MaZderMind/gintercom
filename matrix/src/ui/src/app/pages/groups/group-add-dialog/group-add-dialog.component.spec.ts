import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupAddDialogComponent} from './group-add-dialog.component';
import {MockComponent} from 'ng-mocks';
import {GroupEditComponent} from 'src/app/pages/groups/group-edit/group-edit.component';

describe('GroupAddDialogComponent', () => {
  let component: GroupAddDialogComponent;
  let fixture: ComponentFixture<GroupAddDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GroupAddDialogComponent,
        MockComponent(GroupEditComponent),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupAddDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
