import {Component} from '@angular/core';
import {paymentService, reportItem} from "../service/payment.service";
import {NgbAlert} from "@ng-bootstrap/ng-bootstrap";
import {PosBuddyConstants} from "../posBuddyConstants";


@Component({
  selector: 'app-report',
  standalone: true,
  imports: [
    NgbAlert
  ],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css'
})
export class ReportComponent {

  reportItems: Array<reportItem> = [];
  serverResponse: string = "-"
  confirmError: boolean = false;

  constructor(private paymentService: paymentService) {

  }

  ngAfterViewInit() {
    console.log("get items and dispensing stations")
    this.paymentService
      .getReportItems()
      .subscribe(reportItems => {
          this.reportItems = reportItems;
        }, err => {
          this.confirmError = true;
          this.serverResponse = "Fehler bei laden der Reportliste"
        }
      )
  }


  protected readonly PosBuddyConstants = PosBuddyConstants;
}
