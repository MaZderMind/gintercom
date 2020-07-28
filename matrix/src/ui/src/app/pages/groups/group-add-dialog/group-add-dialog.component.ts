import {Component, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-group-add-dialog',
  templateUrl: './group-add-dialog.component.html',
  styleUrls: ['./group-add-dialog.component.scss']
})
export class GroupAddDialogComponent {
  @ViewChild('addGroupModal')
  private addGroupModal: NgbModal;

  constructor(private modalService: NgbModal) {
  }

  open() {
    this.modalService.open(this.addGroupModal);
  }
}
