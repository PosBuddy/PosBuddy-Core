import {Component, HostListener, inject, TemplateRef, ViewChild} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {
  NgbAccordionDirective,
  NgbAccordionModule,
  NgbAlert,
  NgbCollapse,
  NgbOffcanvas
} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {AllocateIdComponent} from "./allocate-id/allocate-id.component";
import {AddValueComponent} from "./add-value/add-value.component";
import {HttpClientModule} from "@angular/common/http";
import {PayoutComponent} from "./payout/payout.component";
import {DeallocateComponent} from "./deallocate/deallocate.component";
import {RevenueComponent} from "./revenue/revenue.component";
import {ServeComponent} from "./serve/serve.component";
import {paymentService} from "./service/payment.service";
import {version} from '../../package.json';
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {NgClass} from "@angular/common";

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
    PayoutComponent,
    DeallocateComponent,
    RevenueComponent,
    ServeComponent,
    NgbAlert,
    NgbCollapse,
    ZXingScannerModule,
    NgClass
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  @ViewChild('accMenue') accMenue!: NgbAccordionDirective;
  title = 'PosBuddy &#9400; by JK';

  private offcanvasService = inject(NgbOffcanvas);

  servePermission: boolean = false;
  checkoutPermission: boolean = false;
  public isCollapsed: boolean = true;

  constructor(private paymentService: paymentService) {

  }

  ngAfterViewInit() {
    console.log("get permissions from backend based on certificate")
    this.paymentService
      .getPermissions()
      .subscribe(permissions => {
          this.checkoutPermission = permissions.checkoutPermission;
          this.servePermission = permissions.servePermission;
        }, err => {
          console.error("Error at getting permissions from backend:" + err)
        }
      )
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyEvent(event: KeyboardEvent) {
    if (event.altKey || event.ctrlKey || event.metaKey || event.shiftKey) {
      return;
    }
    switch (event.key) {
      case "F1" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF1');
        event.preventDefault();
        break
      }
      case "F2" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF2');
        event.preventDefault();
        break
      }
      case "F3" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF3');
        event.preventDefault();
        break
      }
      case "F4" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF4');
        event.preventDefault();
        break
      }
      case "F5" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF5');
        event.preventDefault();
        break
      }
      case "F6" : {
        this.accMenue.collapseAll();
        this.accMenue.toggle('accF6');
        event.preventDefault();
        break
      }
    }
  }

  protected readonly version = version;

  openFilter(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'})
  }
}
