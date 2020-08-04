import {STOMP_CONFIG} from 'src/app/app-stomp-config';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NavbarComponent} from './navbar/navbar.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {ClientsListComponent} from 'src/app/pages/clients/clients-list.component';
import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {StatusComponent} from './pages/dashboard/status/status.component';
import {HistoryComponent} from './pages/dashboard/history/history.component';
import {LineChartModule} from '@swimlane/ngx-charts';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ClientActionsComponent} from 'src/app/pages/clients/client-actions/client-actions.component';
import {ClientStatusComponent} from 'src/app/pages/clients/client-status/client-status.component';
import {PanelStatusComponent} from './pages/panels/panel-status/panel-status.component';
import {PanelActionsComponent} from './pages/panels/panel-actions/panel-actions.component';
import {GroupActionsComponent} from './pages/groups/group-actions/group-actions.component';
import {GroupEditComponent} from './pages/groups/group-edit/group-edit.component';
import {PanelEditComponent} from './pages/panels/panel-edit/panel-edit.component';
import {ClientViewComponent} from 'src/app/pages/clients/client-view/client-view.component';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';
import {GroupEditDialogComponent} from 'src/app/pages/groups/group-edit-dialog/group-edit-dialog.component';
import {ValidateOnSubmitDirective} from './utils/validate-on-submit.directive';
import {MessagesComponent} from 'src/app/messages/messages.component';
import {HttpErrorShowMessageInterceptor} from 'src/app/messages/http-error-show-message-interceptor';
import {PreventDefaultDirective} from 'src/app/utils/prevent-default.directive';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DashboardComponent,
    GroupsListComponent,
    ClientsListComponent,
    PanelsListComponent,
    StatusComponent,
    HistoryComponent,
    ClientActionsComponent,
    ClientStatusComponent,
    PanelStatusComponent,
    PanelActionsComponent,
    GroupActionsComponent,
    GroupEditComponent,
    PanelEditComponent,
    ClientViewComponent,
    GroupEditDialogComponent,
    ValidateOnSubmitDirective,
    MessagesComponent,
    PreventDefaultDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    NgbModule,
    LineChartModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: InjectableRxStompConfig,
      useValue: STOMP_CONFIG,
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorShowMessageInterceptor,
      multi: true
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
