import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet} from '@angular/router';
import {NgbNavModule} from "@ng-bootstrap/ng-bootstrap";
import {IdentityComponent} from "./identity/identity.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgbNavModule, CommonModule, RouterOutlet, IdentityComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  title = 'posbuddy-gui';
  active = 1;

}
