import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterOutlet} from '@angular/router';
import {NgbNavModule} from "@ng-bootstrap/ng-bootstrap";
import {IdentityComponent} from "./identity/identity.component";
import {HttpClientModule} from "@angular/common/http";
import {MarkdownComponent} from "ngx-markdown";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HttpClientModule,
    NgbNavModule,
    CommonModule,
    RouterOutlet,
    IdentityComponent,
    MarkdownComponent
  ],
  templateUrl: './app.component.html',
})
export class AppComponent {
  title = 'posbuddy-gui';
  active = 1;


}
