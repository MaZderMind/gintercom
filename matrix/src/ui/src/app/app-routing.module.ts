import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from 'src/app/pages/dashboard/dashboard.component';
import {DevicesListComponent} from 'src/app/pages/devices/devices-list.component';
import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {GroupEditComponent} from 'src/app/pages/groups/group-edit/group-edit.component';
import {PanelEditComponent} from 'src/app/pages/panels/panel-edit/panel-edit.component';

const routes: Routes = [
  {path: '', pathMatch: 'full', component: DashboardComponent},
  {path: 'groups', component: GroupsListComponent},
  {path: 'groups/:id', component: GroupEditComponent},
  {path: 'panels', component: PanelsListComponent},
  {path: 'panels/:id', component: PanelEditComponent},
  {path: 'devices', component: DevicesListComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
