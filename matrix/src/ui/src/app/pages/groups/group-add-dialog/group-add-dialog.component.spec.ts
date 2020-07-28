import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupAddDialogComponent } from './group-add-dialog.component';

describe('GroupAddDialogComponent', () => {
  let component: GroupAddDialogComponent;
  let fixture: ComponentFixture<GroupAddDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupAddDialogComponent ]
    })
    .compileComponents();
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
