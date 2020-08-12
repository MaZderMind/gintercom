import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMultiSelectComponent } from './group-multi-select.component';

describe('GroupMultiSelectComponent', () => {
  let component: GroupMultiSelectComponent;
  let fixture: ComponentFixture<GroupMultiSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupMultiSelectComponent ]
    })
    .compileComponents();
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
