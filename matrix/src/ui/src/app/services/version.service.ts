import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VersionService {
  getApplicationVersion(): string {
    let body = document.getElementsByTagName('body')[0];
    return body.dataset['version'];
  }
}
