import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VersionService {
  getApplicationVersion(): string {
    const body = document.getElementsByTagName('body')[0];
    return body.dataset.version;
  }
}
