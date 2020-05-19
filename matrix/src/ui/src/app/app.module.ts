import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NavbarComponent} from './navbar/navbar.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {DevicesListComponent} from 'src/app/pages/devices/devices-list.component';
import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {StatusComponent} from './pages/dashboard/status/status.component';
import {HistoryComponent} from './pages/dashboard/history/history.component';
import {LineChartModule} from '@swimlane/ngx-charts';
import {FormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {DeviceActionsComponent} from './pages/devices/device-actions/device-actions.component';
import {DeviceStatusComponent} from './pages/devices/device-status/device-status.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DashboardComponent,
    GroupsListComponent,
    DevicesListComponent,
    PanelsListComponent,
    StatusComponent,
    HistoryComponent,
    DeviceActionsComponent,
    DeviceStatusComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    NgbModule,
    LineChartModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
