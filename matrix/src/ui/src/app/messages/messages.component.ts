import {Component} from '@angular/core';
import {MessageService} from 'src/app/messages/message.service';
import {Message} from 'src/app/messages/message';

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent {

  constructor(
    private messageService: MessageService
  ) {
  }

  get messages(): Array<Message> {
    return this.messageService.messages;
  }

  hide(message: Message) {
    this.messageService.hide(message);
  }
}
