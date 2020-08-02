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
        catchError((response: HttpErrorResponse) => {
          if (response.error instanceof ErrorEvent) {
            // Client-Side Error
            this.messageService.showError(`Client-Error`, response.error.message);
          } else {
            // Server-Side Error
            const message = response.error.message ?
              response.error.message /* Spring Boot Exception Text */ :
              `HTTP Error Code ${response.status}` /* Generic HTTP Error */;
            this.messageService.showError(message, response.message);
          }

          // Forward to following Handlers
          return throwError(response);
        })
      );
  }
}
