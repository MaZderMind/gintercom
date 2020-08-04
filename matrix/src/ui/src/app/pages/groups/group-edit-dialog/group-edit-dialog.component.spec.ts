import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupEditDialogComponent} from 'src/app/pages/groups/group-edit-dialog/group-edit-dialog.component';
import {MockComponent} from 'ng-mocks';
import {GroupEditComponent} from 'src/app/pages/groups/group-edit/group-edit.component';

describe('GroupEditDialogComponent', () => {
  let component: GroupEditDialogComponent;
  let fixture: ComponentFixture<GroupEditDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GroupEditDialogComponent,
        MockComponent(GroupEditComponent),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
