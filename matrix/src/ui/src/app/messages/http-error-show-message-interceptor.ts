import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {MessageService} from 'src/app/messages/message.service';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HttpErrorShowMessageInterceptor implements HttpInterceptor {
  constructor(
    private messageService: MessageService
  ) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.error instanceof ErrorEvent) {
            // Client-Side Error
            this.messageService.showError(`Client-Error`, error.error.message);
          } else {
            // Server-Side Error
            this.messageService.showError(`HTTP Error Code ${error.status}`, error.message);
          }

          // Forward to following Handlers
          return throwError(error);
        })
      )
  }
}
