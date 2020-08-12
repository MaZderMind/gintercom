import {Injectable} from '@angular/core';
import {Message} from 'src/app/messages/message';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private static readonly HIDE_TIMEOUT_MS = 5000;
  private _messages: Array<Message> = [];

  showInfo(text: string, techDetails?: string) {
    this.show({
      className: 'success',
      hideTimeoutMs: MessageService.HIDE_TIMEOUT_MS,
      text,
      techDetails,
    });
  }

  showWarning(text: string, techDetails?: string) {
    this.show({
      className: 'bg-warning',
      text,
      techDetails
    });
  }

  showError(text: string, techDetails?: string) {
    this.show({
      className: 'bg-danger',
      text,
      techDetails
    });
  }

  show(message: Message) {
    this._messages.push(message);
  }

  hide(message: Message) {
    _.pull(this._messages, message);
  }

  get messages(): Array<Message> {
    return this._messages;
  }
}
