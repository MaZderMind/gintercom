import {Component} from '@angular/core';

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
      link: '/'
    }, {
      title: 'Groups',
      description: 'Intercom Groups',
      icon: 'fas fa-user-friends',
      link: '/groups'
    }, {
      title: 'Panels',
      description: 'Intercom Panels',
      icon: 'fas fa-user',
      link: '/panels'
    }, {
      title: 'Devices',
      description: 'Connected Devices',
      icon: 'fas fa-mobile-alt',
      link: '/devices'
    }
  ];
}

interface NavbarItem {
  title: string;
  description: string;
  icon: string;
  link: string;
}
