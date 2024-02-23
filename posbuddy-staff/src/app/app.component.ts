import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NgbAccordionModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {AllocateIdComponent} from "./allocate-id/allocate-id.component";
import {AddValueComponent} from "./add-value/add-value.component";
import {HttpClientModule} from "@angular/common/http";
import {PayoutComponent} from "./payout/payout.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HttpClientModule,
    NgbAccordionModule,
    RouterOutlet,
    FormsModule,
    AllocateIdComponent,
    AddValueComponent,
    PayoutComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PosBuddy &#9400; by JK';
  version:string ='1.0.0'
}