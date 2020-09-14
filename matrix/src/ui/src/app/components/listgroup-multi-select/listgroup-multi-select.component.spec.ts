import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListgroupMultiSelectComponent } from './listgroup-multi-select.component';

describe('ListgroupMultiSelectComponent', () => {
  let component: ListgroupMultiSelectComponent;
  let fixture: ComponentFixture<ListgroupMultiSelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListgroupMultiSelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListgroupMultiSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
