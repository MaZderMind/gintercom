import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from 'src/app/pages/dashboard/dashboard.component';
import {DevicesListComponent} from 'src/app/pages/deviceslist/devices-list.component';
import {GroupsListComponent} from 'src/app/pages/groupslist/groups-list.component';
import {PanelsListComponent} from 'src/app/pages/panelslist/panels-list.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: DashboardComponent,
  },
  {
    path: 'groups',
    component: GroupsListComponent,
  },
  {
    path: 'panels',
    component: PanelsListComponent,
  },
  {
    path: 'devices',
    component: DevicesListComponent,
  },
  {
    path: '**',
    redirectTo: '',
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
