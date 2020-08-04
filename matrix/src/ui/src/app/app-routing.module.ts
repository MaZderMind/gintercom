import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DashboardComponent} from 'src/app/pages/dashboard/dashboard.component';
import {ClientsListComponent} from 'src/app/pages/clients/clients-list.component';
import {GroupsListComponent} from 'src/app/pages/groups/groups-list.component';
import {PanelsListComponent} from 'src/app/pages/panels/panels-list.component';
import {PanelEditComponent} from 'src/app/pages/panels/panel-edit/panel-edit.component';
import {ClientViewComponent} from 'src/app/pages/clients/client-view/client-view.component';

const routes: Routes = [
  {path: '', pathMatch: 'full', component: DashboardComponent},
  {path: 'groups', component: GroupsListComponent},
  {path: 'panels', component: PanelsListComponent},
  {path: 'panels/:id', component: PanelEditComponent},
  {path: 'clients', component: ClientsListComponent},
  {path: 'clients/:id', component: ClientViewComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
