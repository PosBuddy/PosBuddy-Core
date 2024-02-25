import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {paymentService} from "../service/payment.service";


@Component({
  selector: 'app-revenue',
  standalone: true,
  imports: [
    NgbAlert,
    ReactiveFormsModule,
    ZXingScannerModule,
    FormsModule
  ],
  templateUrl: './revenue.component.html',
  styleUrl: './revenue.component.css'
})
export class RevenueComponent {
  constructor(private paymentService: paymentService) {
  }

  private offcanvasService = inject(NgbOffcanvas);
  payoutPossible = false;
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  formValid: boolean = false;
  formValidText: string = "ID zur Anzeige der Umsätze scannen"
  posBuddyId: string = "-";
  value = '0';

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'})
  }


  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.formValid = true
    this.offcanvasService.dismiss("success");
  }


  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.value = "0";
    this.posBuddyId = "-";
    this.payoutPossible = false;
  }


}
