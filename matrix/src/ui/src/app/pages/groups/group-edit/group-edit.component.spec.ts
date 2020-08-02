import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupEditComponent} from './group-edit.component';
import {ActivatedRoute} from '@angular/router';
import {EMPTY} from 'rxjs';
import {GroupsService} from 'src/app/services/groups/groups.service';
import {instance, mock} from 'ts-mockito';
import {ReactiveFormsModule} from '@angular/forms';

describe('GroupEditComponent', () => {
  let component: GroupEditComponent;
  let fixture: ComponentFixture<GroupEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GroupEditComponent],
      providers: [
        {
          provide: ActivatedRoute, useValue: {
            paramMap: EMPTY,
          }
        },
        {provide: GroupsService, useFactory: () => instance(mock(GroupsService))},
      ],
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
