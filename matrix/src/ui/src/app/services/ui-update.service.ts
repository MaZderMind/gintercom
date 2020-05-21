import {Injectable} from '@angular/core';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Subject} from 'rxjs';
import {Subscription} from 'rxjs/internal/Subscription';

@Injectable({
  providedIn: 'root'
})
export class UiUpdateService {
  private doUpdateUi = new Subject<void>();

  constructor(stompService: RxStompService) {
    console.log('stompService.watch');
    stompService.watch('/ui/update').subscribe((message: any) => {
      this.doUpdateUi.next(null);
    });
  }

  subscribe(observer: () => void): Subscription {
    return this.doUpdateUi.subscribe(observer);
  }
}
