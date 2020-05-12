import {Component} from '@angular/core';
import {VersionService} from 'src/app/services/version.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  readonly items: NavbarItem[] = [
    {
      title: 'Dashboard',
      description: 'System Status & Statistics',
      icon: 'fas fa-tachometer-alt',
      link: '/',
      exact: true
    }, {
      title: 'Devices',
      description: 'Connected Devices',
      icon: 'fas fa-mobile-alt',
      link: '/devices'
    }, {
      title: 'Panels',
      description: 'Intercom Panels',
      icon: 'fas fa-user',
      link: '/panels'
    }, {
      title: 'Groups',
      description: 'Intercom Groups',
      icon: 'fas fa-user-friends',
      link: '/groups'
    }
  ];

  readonly isMobile = ('ontouchstart' in window);

  applicationVersion: string;

  constructor(private versionService: VersionService) {
    this.applicationVersion = versionService.getApplicationVersion();
  }
}

interface NavbarItem {
  title: string;
  description: string;
  icon: string;
  link: string;
  exact?: boolean;
}
