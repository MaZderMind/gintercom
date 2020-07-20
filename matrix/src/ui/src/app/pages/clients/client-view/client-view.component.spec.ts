import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClientViewComponent} from 'src/app/pages/clients/client-view/client-view.component';
import {ActivatedRoute} from '@angular/router';
import {EMPTY} from 'rxjs';

describe('ClientViewComponent', () => {
  let component: ClientViewComponent;
  let fixture: ComponentFixture<ClientViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClientViewComponent],
      providers: [
        {
          provide: ActivatedRoute, useValue: {
            paramMap: EMPTY,
          }
        },
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
