import {Component, Input, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-group-edit-dialog',
  templateUrl: './group-edit-dialog.component.html',
  styleUrls: ['./group-edit-dialog.component.scss']
})
export class GroupEditDialogComponent {
  @ViewChild('modal')
  private modal: NgbModal;

  @Input()
  groupId: string;

  constructor(private modalService: NgbModal) {
  }

  isCreation(): boolean {
    return !this.groupId;
  }

  open(groupId?: string) {
    this.groupId = groupId;
    this.modalService.open(this.modal);
  }
}
