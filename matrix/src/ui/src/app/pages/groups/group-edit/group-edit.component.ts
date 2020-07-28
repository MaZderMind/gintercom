import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent implements OnInit {
  groupEditForm = new FormGroup({
    groupId: new FormControl('', [Validators.required]),
    displayName: new FormControl(''),
  });

  groupId: string;

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.groupId = params.get('id');
    });
  }

  onSubmit() {
    console.log('validSubmit');
  }
}
